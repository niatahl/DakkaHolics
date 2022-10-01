//By Nicke535, licensed under CC-BY-NC-SA 4.0 (https://creativecommons.org/licenses/by-nc-sa/4.0/)
//General script meant to be modified for each implementation. Causes a projectile to rotate mid-flight depending on several settings, simulating guidance
//HOW TO USE:
//	Copy this file where you want it and rename+adjust values
//	Find the projectile to guide using any method you want (everyframe script, weapon-mounted everyframe script, mine-spawning etc.)
//	run "engine.addPlugin(tahlan_BalorProjectileScript(proj, target));" with:
//		tahlan_BalorProjectileScript being replaced with your new class name
//		proj being the projectile to guide
//		target being the initial target (if any)
//	You're done!
package data.scripts.weapons

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin
import com.fs.starfarer.api.combat.CombatEntityAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.CollisionUtils
import org.lazywizard.lazylib.FastTrig
import org.lazywizard.lazylib.MathUtils
import org.lazywizard.lazylib.VectorUtils
import org.lazywizard.lazylib.combat.CombatUtils
import org.lwjgl.util.vector.Vector2f

class dakka_ProjectileGuidancePlugin(proj: DamagingProjectileAPI, target: CombatEntityAPI?) : BaseEveryFrameCombatPlugin() {
    //---Internal script variables: don't touch!---
    private val proj //The projectile itself
            : DamagingProjectileAPI?
    private var target // Current target of the projectile
            : CombatEntityAPI?
    private var targetPoint // For ONE_TURN_TARGET, actual target position. Otherwise, an offset from the target's "real" position. Not used for ONE_TURN_DUMB
            : Vector2f? = null
    private var targetAngle // Only for ONE_TURN_DUMB, the target angle that we want to hit with the projectile
            = 0f
    private var swayCounter1 // Counter for handling primary sway
            : Float
    private var swayCounter2 // Counter for handling secondary sway
            : Float
    private var lifeCounter // Keeps track of projectile lifetime
            : Float
    private val estimateMaxLife // How long we estimate this projectile should be alive
            : Float
    private var delayCounter // Counter for delaying targeting
            : Float
    private var offsetVelocity // Only used for ONE_TURN_DUMB: keeps velocity from the ship and velocity from the projectile separate (messes up calculations otherwise)
            : Vector2f? = null
    private var lastTargetPos // The last position our target was located at, for target-reacquiring purposes
            : Vector2f
    private val actualGuidanceDelay // The actual guidance delay for this specific projectile
            : Float

    /**
     * Initializer for the guided projectile script
     *
     * @param proj   The projectile to affect. proj.getWeapon() must be non-null.
     * @param target The target missile/asteroid/ship for the script's guidance.
     * Can be null, if the script does not follow a target ("ONE_TURN_DUMB") or to instantly activate secondary guidance mode.
     */
    init {
        this.proj = proj
        this.target = target
        lastTargetPos = if (target != null) target.location else Vector2f(proj.location)
        swayCounter1 = MathUtils.getRandomNumberInRange(0f, 1f)
        swayCounter2 = MathUtils.getRandomNumberInRange(0f, 1f)
        lifeCounter = 0f
        estimateMaxLife = proj.weapon.range / Vector2f(proj.velocity.x - proj.source.velocity.x, proj.velocity.y - proj.source.velocity.y).length()
        delayCounter = 0f
        actualGuidanceDelay = MathUtils.getRandomNumberInRange(GUIDANCE_DELAY_MIN, GUIDANCE_DELAY_MAX)

        //For one-turns, we set our target point ONCE and never adjust it
        if (GUIDANCE_MODE_PRIMARY == "ONE_TURN_DUMB") {
            targetAngle = proj.weapon.currAngle + MathUtils.getRandomNumberInRange(-ONE_TURN_DUMB_INACCURACY, ONE_TURN_DUMB_INACCURACY)
            offsetVelocity = proj.source.velocity
        } else if (GUIDANCE_MODE_PRIMARY == "ONE_TURN_TARGET") {
            targetPoint = MathUtils.getRandomPointInCircle(getApproximateInterception(25), ONE_TURN_TARGET_INACCURACY)
        } else if (GUIDANCE_MODE_PRIMARY.contains("SWARM") && target != null) {
            applySwarmOffset()
        } else {
            targetPoint = Vector2f(Misc.ZERO)
        }
    }

    //Main advance method
    override fun advance(amount: Float, events: List<InputEventAPI>) {
        //Sanity checks
        var actualAmount = amount
        if (Global.getCombatEngine() == null) {
            return
        }
        if (Global.getCombatEngine().isPaused) {
            actualAmount = 0f
        }

        //Checks if our script should be removed from the combat engine
        if (proj == null || proj.didDamage() || proj.isFading || !Global.getCombatEngine().isEntityInPlay(proj)) {
            Global.getCombatEngine().removePlugin(this)
            return
        }

        //Ticks up our life counter: if we miscalculated, also top it off
        lifeCounter += actualAmount
        if (lifeCounter > estimateMaxLife) {
            lifeCounter = estimateMaxLife
        }

        //Delays targeting if we have that enabled
        if (delayCounter < actualGuidanceDelay) {
            delayCounter += actualAmount
            return
        }

        //Tick the sway counter up here regardless of if we need it or not: helps reduce boilerplate code
        swayCounter1 += actualAmount * SWAY_PERIOD_PRIMARY
        swayCounter2 += actualAmount * SWAY_PERIOD_SECONDARY
        val swayThisFrame = Math.pow((1f - lifeCounter / estimateMaxLife).toDouble(), SWAY_FALLOFF_FACTOR.toDouble()).toFloat() *
                ((FastTrig.sin(Math.PI * 2f * swayCounter1) * SWAY_AMOUNT_PRIMARY).toFloat() + (FastTrig.sin(Math.PI * 2f * swayCounter2) * SWAY_AMOUNT_SECONDARY).toFloat())

        //First: are we a one-turn? in that case, skip secondary targeting
        if (!GUIDANCE_MODE_PRIMARY.contains("ONE_TURN")) {
            //Check if we need to find a new target
            if (target != null) {
                if (!Global.getCombatEngine().isEntityInPlay(target)) {
                    target = null
                }
                if (target is ShipAPI) {
                    if ((target as ShipAPI).isHulk || (target as ShipAPI).isPhased && BROKEN_BY_PHASE || (target as ShipAPI).getOwner() == proj.owner && RETARGET_ON_SIDE_SWITCH) {
                        target = null
                    }
                }
            }

            //If we need to retarget, check our retarget strategy and act accordingly
            if (target == null) {
                //With no retarget plan, the script just shuts itself off
                if (GUIDANCE_MODE_SECONDARY == "NONE") {
                    Global.getCombatEngine().removePlugin(this)
                    return
                } else if (GUIDANCE_MODE_SECONDARY == "DISAPPEAR") {
                    Global.getCombatEngine().removeEntity(proj)
                    Global.getCombatEngine().removePlugin(this)
                    return
                } else {
                    reacquireTarget()
                }
            } else {
                lastTargetPos = Vector2f(target!!.location)
            }
        }

        //If we're using anything that needs a target, and our retargeting failed, just head in a straight line: no script is run
        if (!GUIDANCE_MODE_PRIMARY.contains("ONE_TURN") && target == null) {
            return
        } else {
            val actualTurnRate = TURN_RATE * proj.velocity.length()

            //Dumb one-turns just turn toward an angle, though they also need to compensate for offset velocity to remain straight
            if (GUIDANCE_MODE_PRIMARY == "ONE_TURN_DUMB") {
                var facingSwayless = proj.facing - swayThisFrame
                var angleDiffAbsolute = Math.abs(targetAngle - facingSwayless)
                while (angleDiffAbsolute > 180f) {
                    angleDiffAbsolute = Math.abs(angleDiffAbsolute - 360f)
                }
                facingSwayless += Misc.getClosestTurnDirection(facingSwayless, targetAngle) * Math.min(angleDiffAbsolute, actualTurnRate * actualAmount)
                val pureVelocity = Vector2f(proj.velocity)
                pureVelocity.x -= offsetVelocity!!.x
                pureVelocity.y -= offsetVelocity!!.y
                proj.facing = facingSwayless + swayThisFrame
                proj.velocity.x = MathUtils.getPoint(Vector2f(Misc.ZERO), pureVelocity.length(), facingSwayless + swayThisFrame).x + offsetVelocity!!.x
                proj.velocity.y = MathUtils.getPoint(Vector2f(Misc.ZERO), pureVelocity.length(), facingSwayless + swayThisFrame).y + offsetVelocity!!.y
            } else if (GUIDANCE_MODE_PRIMARY == "ONE_TURN_TARGET") {
                var facingSwayless = proj.facing - swayThisFrame
                val angleToHit = VectorUtils.getAngle(proj.location, targetPoint)
                var angleDiffAbsolute = Math.abs(angleToHit - facingSwayless)
                while (angleDiffAbsolute > 180f) {
                    angleDiffAbsolute = Math.abs(angleDiffAbsolute - 360f)
                }
                facingSwayless += Misc.getClosestTurnDirection(facingSwayless, angleToHit) * Math.min(angleDiffAbsolute, actualTurnRate * actualAmount)
                proj.facing = facingSwayless + swayThisFrame
                proj.velocity.x = MathUtils.getPoint(Vector2f(Misc.ZERO), proj.velocity.length(), facingSwayless + swayThisFrame).x
                proj.velocity.y = MathUtils.getPoint(Vector2f(Misc.ZERO), proj.velocity.length(), facingSwayless + swayThisFrame).y
            } else if (GUIDANCE_MODE_PRIMARY.contains("DUMBCHASER")) {
                var facingSwayless = proj.facing - swayThisFrame
                val targetPointRotated = VectorUtils.rotate(Vector2f(targetPoint), target!!.facing)
                val angleToHit = VectorUtils.getAngle(proj.location, Vector2f.add(target!!.location, targetPointRotated, Vector2f(Misc.ZERO)))
                var angleDiffAbsolute = Math.abs(angleToHit - facingSwayless)
                while (angleDiffAbsolute > 180f) {
                    angleDiffAbsolute = Math.abs(angleDiffAbsolute - 360f)
                }
                facingSwayless += Misc.getClosestTurnDirection(facingSwayless, angleToHit) * Math.min(angleDiffAbsolute, actualTurnRate * actualAmount)
                proj.facing = facingSwayless + swayThisFrame
                proj.velocity.x = MathUtils.getPoint(Vector2f(Misc.ZERO), proj.velocity.length(), facingSwayless + swayThisFrame).x
                proj.velocity.y = MathUtils.getPoint(Vector2f(Misc.ZERO), proj.velocity.length(), facingSwayless + swayThisFrame).y
            } else if (GUIDANCE_MODE_PRIMARY.contains("INTERCEPT")) {
                //We use fewer calculation steps for projectiles that are very close, as they aren't needed at close distances
                val iterations = INTERCEPT_ITERATIONS
                var facingSwayless = proj.facing - swayThisFrame
                val targetPointRotated = VectorUtils.rotate(Vector2f(targetPoint), target!!.facing)
                val angleToHit = VectorUtils.getAngle(proj.location, Vector2f.add(getApproximateInterception(iterations), targetPointRotated, Vector2f(Misc.ZERO)))
                var angleDiffAbsolute = Math.abs(angleToHit - facingSwayless)
                while (angleDiffAbsolute > 180f) {
                    angleDiffAbsolute = Math.abs(angleDiffAbsolute - 360f)
                }
                facingSwayless += Misc.getClosestTurnDirection(facingSwayless, angleToHit) * Math.min(angleDiffAbsolute, actualTurnRate * actualAmount)
                proj.facing = facingSwayless + swayThisFrame
                proj.velocity.x = MathUtils.getPoint(Vector2f(Misc.ZERO), proj.velocity.length(), facingSwayless + swayThisFrame).x
                proj.velocity.y = MathUtils.getPoint(Vector2f(Misc.ZERO), proj.velocity.length(), facingSwayless + swayThisFrame).y
            }
        }
    }

    //Re-acquires a target depending on re-acquiring strategy
    private fun reacquireTarget() {
        var newTarget: CombatEntityAPI? = null
        var centerOfDetection: Vector2f? = lastTargetPos
        if (GUIDANCE_MODE_SECONDARY.contains("_PROJ")) {
            centerOfDetection = proj!!.location
        }
        val potentialTargets: MutableList<CombatEntityAPI> = ArrayList()
        if (VALID_TARGET_TYPES.contains("ASTEROID")) {
            for (potTarget in CombatUtils.getAsteroidsWithinRange(centerOfDetection, TARGET_REACQUIRE_RANGE)) {
                if (potTarget.owner != proj!!.owner && Math.abs(VectorUtils.getAngle(proj.location, potTarget.location) - proj.facing) < TARGET_REACQUIRE_ANGLE) {
                    potentialTargets.add(potTarget)
                }
            }
        }
        if (VALID_TARGET_TYPES.contains("MISSILE")) {
            for (potTarget in CombatUtils.getMissilesWithinRange(centerOfDetection, TARGET_REACQUIRE_RANGE)) {
                if (potTarget.owner != proj!!.owner && Math.abs(VectorUtils.getAngle(proj.location, potTarget.location) - proj.facing) < TARGET_REACQUIRE_ANGLE) {
                    potentialTargets.add(potTarget)
                }
            }
        }
        for (potTarget in CombatUtils.getShipsWithinRange(centerOfDetection, TARGET_REACQUIRE_RANGE)) {
            if (potTarget.owner == proj!!.owner || Math.abs(VectorUtils.getAngle(proj.location, potTarget.location) - proj.facing) > TARGET_REACQUIRE_ANGLE || potTarget.isHulk) {
                continue
            }
            if (potTarget.isPhased && BROKEN_BY_PHASE) {
                continue
            }
            if (potTarget.hullSize == ShipAPI.HullSize.FIGHTER && VALID_TARGET_TYPES.contains("FIGHTER")) {
                potentialTargets.add(potTarget)
            }
            if (potTarget.hullSize == ShipAPI.HullSize.FRIGATE && VALID_TARGET_TYPES.contains("FRIGATE")) {
                potentialTargets.add(potTarget)
            }
            if (potTarget.hullSize == ShipAPI.HullSize.DESTROYER && VALID_TARGET_TYPES.contains("DESTROYER")) {
                potentialTargets.add(potTarget)
            }
            if (potTarget.hullSize == ShipAPI.HullSize.CRUISER && VALID_TARGET_TYPES.contains("CRUISER")) {
                potentialTargets.add(potTarget)
            }
            if (potTarget.hullSize == ShipAPI.HullSize.CAPITAL_SHIP && VALID_TARGET_TYPES.contains("CAPITAL")) {
                potentialTargets.add(potTarget)
            }
        }
        //If we found any eligible target, continue selection, otherwise we'll have to stay with no target
        if (!potentialTargets.isEmpty()) {
            if (GUIDANCE_MODE_SECONDARY.contains("REACQUIRE_NEAREST")) {
                for (potTarget in potentialTargets) {
                    if (newTarget == null) {
                        newTarget = potTarget
                    } else if (MathUtils.getDistance(newTarget, centerOfDetection) > MathUtils.getDistance(potTarget, centerOfDetection)) {
                        newTarget = potTarget
                    }
                }
            } else if (GUIDANCE_MODE_SECONDARY.contains("REACQUIRE_RANDOM")) {
                newTarget = potentialTargets[MathUtils.getRandomNumberInRange(0, potentialTargets.size - 1)]
            }

            //Once all that is done, set our target to the new target and select a new swarm point (if appropriate)
            target = newTarget
            if (GUIDANCE_MODE_PRIMARY.contains("SWARM")) {
                applySwarmOffset()
            }
        }
    }

    //Iterative intercept point calculation: has option for taking more or less calculation steps to trade calculation speed for accuracy
    private fun getApproximateInterception(calculationSteps: Int): Vector2f {
        val returnPoint = Vector2f(target!!.location)

        //Iterate a set amount of times, improving accuracy each time
        for (i in 0 until calculationSteps) {
            //Get the distance from the current iteration point and the projectile, and calculate the approximate arrival time
            val arrivalTime = MathUtils.getDistance(proj!!.location, returnPoint) / proj.velocity.length()

            //Calculate the targeted point with this arrival time
            returnPoint.x = target!!.location.x + target!!.velocity.x * arrivalTime * INTERCEPT_ACCURACY_FACTOR
            returnPoint.y = target!!.location.y + target!!.velocity.y * arrivalTime * INTERCEPT_ACCURACY_FACTOR
        }
        return returnPoint
    }

    //Used for getting a swarm target point, IE a random point offset on the target. Should only be used when target != null
    private fun applySwarmOffset() {
        var i = 40 //We don't want to take too much time, even if we get unlucky: only try 40 times
        var success = false
        while (i > 0 && target != null) {
            i--

            //Get a random position and check if its valid
            var potPoint = MathUtils.getRandomPointInCircle(target!!.location, target!!.collisionRadius)
            if (CollisionUtils.isPointWithinBounds(potPoint, target)) {
                //If the point is valid, convert it to an offset and store it
                potPoint.x -= target!!.location.x
                potPoint.y -= target!!.location.y
                potPoint = VectorUtils.rotate(potPoint, -target!!.facing)
                targetPoint = Vector2f(potPoint)
                success = true
                break
            }
        }

        //If we didn't find a point in 40 tries, just choose target center
        if (!success) {
            targetPoint = Vector2f(Misc.ZERO)
        }
    }

    companion object {
        //---Settings: adjust to fill the needs of your implementation---
        //Sets guidance mode for the projectile when a target is fed to the script (or, in the case of ONE_TURN_DUMB, always).
        //Note that interceptor-style projectiles use notably more memory than the other types (as they practically run missile AI), so they should be used sparingly
        //Possible values are:
        //	- "ONE_TURN_DUMB" : Turns towards weapon facing at time of firing. Never readjusts afterwards, and completely ignores target
        //	- "ONE_TURN_TARGET" : Turns towards an approximate intercept point with the target, but never readjusts after the first turn. Ignores loosing the target
        //	- "DUMBCHASER" : Heads straight for the target's center at all times
        //	- "DUMBCHASER_SWARM" : As DUMBCHASER, but targets a random point on the target instead of the center, determined at target acquisition
        //	- "INTERCEPT" : Heads for an approximate intercept point of the target at all times. Becomes more accurate as distance to target decreases
        //	- "INTERCEPT_SWARM" : As INTERCEPT, but targets a random point on the target instead of the center, determined at target acquisition
        private const val GUIDANCE_MODE_PRIMARY = "DUMBCHASER"

        //Sets behaviour when the original target is lost; if this is a target re-acquiring method, GUIDANCE_MODE_PRIMARY takes effect again with the new target.
        //Note that if there is no target within TARGET_REACQUIRE_RANGE, "NONE" is the default behaviour for re-acquires until a target is found
        //Possible values are:
        //	- "NONE" : Turn off all script behaviour when loosing your target
        //	- "REACQUIRE_NEAREST" : Selects the nearest valid target to the original target's position
        //	- "REACQUIRE_NEAREST_PROJ" : Selects the nearest valid target to the *projectile's* position
        //	- "REACQUIRE_RANDOM" : Selects a random valid target within TARGET_REACQUIRE_RANGE of the target
        //	- "REACQUIRE_RANDOM_PROJ" : Selects a random valid target within TARGET_REACQUIRE_RANGE of the projectile
        //	- "DISAPPEAR" : Remove the projectile altogether. Should only be used if the projectile has a scripted effect on-death of some sort (such as hand-scripted flak)
        private const val GUIDANCE_MODE_SECONDARY = "NONE"

        //Determines all valid target types for this projectile's target re-acquiring. Only used if the projectile actually uses re-acquiring of targets
        //Possible values are:
        //	- "ASTEROID"
        //	- "MISSILE"
        //	- "FIGHTER"
        //	- "FRIGATE"
        //	- "DESTROYER"
        //	- "CRUISER"
        //	- "CAPITAL"
        private val VALID_TARGET_TYPES: MutableList<String> = ArrayList()

        init {
            VALID_TARGET_TYPES.add("FRIGATE")
            VALID_TARGET_TYPES.add("DESTROYER")
            VALID_TARGET_TYPES.add("CRUISER")
            VALID_TARGET_TYPES.add("CAPITAL")
        }

        //The maximum range a target can be re-acquired at, in SU.
        //Note that this is counted from the *original* target by default, not the projectile itself (use _PROJ) for that behaviour
        private const val TARGET_REACQUIRE_RANGE = 1250f

        //The maximum angle a target can be re-acquired at, in degrees.
        //90 means 90 degrees to either side, I.E. a hemisphere in front of the projectile. Values 180 and above turns off the limitation altogether
        private const val TARGET_REACQUIRE_ANGLE = 90f

        //How fast the projectile is allowed to turn, in degrees/second
        private const val TURN_RATE = 0.05f

        //If non-zero, the projectile will sway back-and-forth by this many degrees during its guidance (with a sway period determined by SWAY_PERIOD).
        //High values, as one might expect, give very poor tracking. Also, high values will decrease effective range (as the projectiles travel further) so be careful
        //Secondary and primary sway both run in parallel, allowing double-sine swaying if desired
        private const val SWAY_AMOUNT_PRIMARY = 0f
        private const val SWAY_AMOUNT_SECONDARY = 0f

        //Used together with SWAY_AMOUNT, determines how fast the swaying happens
        //1f means an entire sway "loop" (max sway right -> min sway -> max sway left -> min sway again) per second, 2f means 2 loops etc.
        //Projectiles start at a random position in their sway loop on spawning
        //Secondary and primary sway both run in parallel, allowing double-sine swaying if desired
        private const val SWAY_PERIOD_PRIMARY = 1.4f
        private const val SWAY_PERIOD_SECONDARY = 3f

        //How fast, if at all, sway falls off with the projectile's lifetime.
        //At 1f, it's a linear falloff, at 2f it's quadratic. At 0f, there is no falloff
        private const val SWAY_FALLOFF_FACTOR = 0f

        //Only used for ONE_TURN_DUMB: the actual target angle is randomly offset by this much, to simulate inaccuracy
        //2f means up to 2 degrees angle off from the actual target angle
        private const val ONE_TURN_DUMB_INACCURACY = 0f

        //Only used for ONE_TURN_TARGET: the actual target point is randomly offset by this many SU, to simulate inaccuracy
        //20f means up to 20 SU away from the actual target point
        private const val ONE_TURN_TARGET_INACCURACY = 0f

        //Only used for the INTERCEPT targeting types: number of iterations to run for calculations.
        //At 0 it's indistinguishable from a dumbchaser, at 15 it's frankly way too high. 4-7 recommended for slow weapons, 2-3 for weapons with more firerate/lower accuracy
        private const val INTERCEPT_ITERATIONS = 3

        //Only used for the INTERCEPT targeting type: a factor for how good the AI judges target leading
        //At 1f it tries to shoot the "real" intercept point, while at 0f it's indistinguishable from a dumbchaser.
        private const val INTERCEPT_ACCURACY_FACTOR = 1f

        //Delays the activation of the script by a random amount of seconds between this MIN and MAX.
        //Note that ONE_TURN shots will still decide on target angle/point at spawn-time, not when this duration is up
        private const val GUIDANCE_DELAY_MAX = 0f
        private const val GUIDANCE_DELAY_MIN = 0f

        //Whether phased ships are ignored for targeting (and an already phased target counts as "lost" and procs secondary targeting)
        private const val BROKEN_BY_PHASE = true

        //Whether the projectile switches to a new target if the current one becomes an ally
        private const val RETARGET_ON_SIDE_SWITCH = false
    }
}
package org.niatahl.dakka.weapons

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import data.scripts.util.MagicRender
import org.lazywizard.lazylib.VectorUtils
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class MjolnirOnFireEffect : OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
    private var timeLeft = 0f
    private var currentBarrel = 1
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        val point = projectile.location
        engine.spawnExplosion(point, weapon.ship.velocity, Color(169, 154, 255, 120), 60f, 0.15f)
        timeLeft = ANIMATION_TIME / weapon.ship.mutableStats.ballisticRoFMult.modifiedValue
        currentBarrel = if (currentBarrel == 0) 1 else 0
    }

    override fun advance(amount: Float, engine: CombatEngineAPI, weapon: WeaponAPI) {
        if (timeLeft > 0f) {
            timeLeft -= amount

            //Muzzle location calculation
            var point = Vector2f()
            if (weapon.slot.isHardpoint) {
                point.x = weapon.spec.hardpointFireOffsets[currentBarrel].x
                point.y = weapon.spec.hardpointFireOffsets[currentBarrel].y
            } else if (weapon.slot.isTurret) {
                point.x = weapon.spec.turretFireOffsets[currentBarrel].x
                point.y = weapon.spec.turretFireOffsets[currentBarrel].y
            } else {
                point.x = weapon.spec.hiddenFireOffsets[currentBarrel].x
                point.y = weapon.spec.hiddenFireOffsets[currentBarrel].y
            }
            point = VectorUtils.rotate(point, weapon.currAngle, Vector2f(0f, 0f))
            point.x += weapon.location.x
            point.y += weapon.location.y
            var offset: Vector2f? = Vector2f(5f, 0f)
            offset = VectorUtils.rotate(offset, weapon.currAngle, Vector2f(0f, 0f))
            Vector2f.add(point, offset, point)
            val alpha = (230f - 220f / ANIMATION_TIME * (ANIMATION_TIME - timeLeft)).toInt().coerceIn(0,255)
            MagicRender.singleframe(
                Global.getSettings().getSprite("fx", "dakka_shockwave"),
                point,
                Vector2f(
                    9f + 60f / ANIMATION_TIME * (ANIMATION_TIME - timeLeft),
                    3f + 20f / ANIMATION_TIME * (ANIMATION_TIME - timeLeft)
                ),
                weapon.currAngle + 90f,
                Color(200, 210, 255, alpha),
                true
            )
        }
    }

    companion object {
        private const val ANIMATION_TIME = 0.5f
    }
}
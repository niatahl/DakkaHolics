package data.scripts.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnFireEffectPlugin
import com.fs.starfarer.api.combat.WeaponAPI
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class dakka_HellboreOnFireEffect : OnFireEffectPlugin {
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        val effectCol = Color(-0x5f259ef5, true)
        engine.addHitParticle(
                projectile.location,
                weapon.ship.velocity,
                120f,
                1f,
                0.1f,
                effectCol.brighter()
        )
        var projVel: Vector2f
        var smokeVel: Vector2f
        val num = if (weapon.size == WeaponAPI.WeaponSize.LARGE) 10 else 5
        val sizeMult = if (weapon.size == WeaponAPI.WeaponSize.LARGE) 2f else 1f
        for (i in 0 until num) {
            projVel = Vector2f()
            smokeVel = Vector2f()
            projectile.velocity.normalise(projVel)
            projVel.scale(MathUtils.getRandomNumberInRange(0f, 30f))
            Vector2f.add(weapon.ship.velocity, projVel, smokeVel)
            engine.addNebulaParticle(
                    projectile.location,
                    smokeVel,
                    MathUtils.getRandomNumberInRange(20f * sizeMult, 30f * sizeMult),
                    1.5f,
                    0.1f,
                    0.3f,
                    MathUtils.getRandomNumberInRange(2f, 2.5f),
                    Color(50, 48, 45, 120),
                    true
            )
        }
        engine.spawnExplosion(
                projectile.location,
                weapon.ship.velocity,
                effectCol.brighter(),
                50f,
                0.2f
        )
    }
}
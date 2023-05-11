package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnFireEffectPlugin
import com.fs.starfarer.api.combat.WeaponAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class GigaOnFireEffect : OnFireEffectPlugin {
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        val effectCol = projectile.projectileSpec.glowColor
        engine.spawnExplosion(projectile.location, weapon.ship.velocity, projectile.projectileSpec.coreColor, 80f, 0.15f)
        for (i in 1..3)
            engine.addNebulaParticle(
                projectile.location,
                weapon.ship.velocity,
                30f * i,
                1.5f,
                0.1f,
                0.2f,
                1f,
                Color(220,220,220,100)
            )
        engine.addHitParticle(
            projectile.location,
            Misc.ZERO,
            200f,
            0.8f,
            0.1f,
            effectCol
        )
        engine.addSmoothParticle(
            projectile.location,
            Misc.ZERO,
            300f,
            0.8f,
            0.1f,
            effectCol
        )
        for (i in 1..3)
            engine.addSwirlyNebulaParticle(projectile.location, weapon.ship.velocity, 30f * i/2, 1.5f, 0.1f, 0.2f, 0.4f, projectile.projectileSpec.fringeColor, true)
        for (i in 0 until 10) {
            val projVel = Vector2f()
            val smokeVel = Vector2f()
            projectile.velocity.normalise(projVel)
            projVel.scale(MathUtils.getRandomNumberInRange(0f, 30f))
            Vector2f.add(weapon.ship.velocity, projVel, smokeVel)
            engine.addNebulaParticle(
                projectile.location,
                smokeVel,
                MathUtils.getRandomNumberInRange(20f, 30f),
                1.5f,
                0.1f,
                0.3f,
                MathUtils.getRandomNumberInRange(2f, 2.5f),
                Color(50, 48, 45, 120),
                true
            )
        }
    }

    companion object {
        const val NUM_PARTICLES = 100
    }
}
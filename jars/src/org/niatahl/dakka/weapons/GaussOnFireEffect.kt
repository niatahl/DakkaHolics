package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnFireEffectPlugin
import com.fs.starfarer.api.combat.WeaponAPI
import org.lazywizard.lazylib.MathUtils
import java.awt.Color

class GaussOnFireEffect : OnFireEffectPlugin {
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        engine.spawnExplosion(projectile.location, weapon.ship.velocity, Color(154, 211, 255, 120), 20f, 0.15f)
        for (i in 0 until NUM_PARTICLES) {
            val arcPoint = MathUtils.getRandomNumberInRange(projectile.facing - 5f, projectile.facing + 5f)
            val velocity = MathUtils.getPointOnCircumference(
                weapon.ship.velocity,
                MathUtils.getRandomNumberInRange(0f, 10f),
                MathUtils.getRandomNumberInRange(projectile.facing - 90f, projectile.facing + 90f)
            )
            var spawnLocation = MathUtils.getPointOnCircumference(
                projectile.location,
                MathUtils.getRandomNumberInRange(0f, 110f),
                arcPoint
            )
            spawnLocation = MathUtils.getRandomPointInCircle(spawnLocation, MathUtils.getRandomNumberInRange(0f, 5f))
            engine.addHitParticle(spawnLocation, velocity,
                MathUtils.getRandomNumberInRange(2f, 3f), 20f,
                MathUtils.getRandomNumberInRange(0.5f, 3f),
                Color(151, 180, 255, 255)
            )
            //engine.addSmoothParticle(spawnLocation, velocity, MathUtils.getRandomNumberInRange(2f,4f), 15f, MathUtils.getRandomNumberInRange(0.5f,3f),new Color(151, 180, 255,255));
        }
    }

    companion object {
        const val NUM_PARTICLES = 100
    }
}
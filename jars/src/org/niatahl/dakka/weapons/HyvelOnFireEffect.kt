package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnFireEffectPlugin
import com.fs.starfarer.api.combat.WeaponAPI
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class HyvelOnFireEffect : OnFireEffectPlugin {
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        val effectCol = Color(110,170,255,100)
        engine.addHitParticle(
            projectile.location,
            weapon.ship.velocity,
            80f,
            1f,
            0.1f,
            effectCol.brighter()
        )
        for (i in 0 until 10) {
            val projVel = Vector2f()
            val smokeVel = Vector2f()
            projectile.velocity.normalise(projVel)
            projVel.scale(MathUtils.getRandomNumberInRange(0f, 30f))
            Vector2f.add(weapon.ship.velocity, projVel, smokeVel)
            engine.addNebulaParticle(
                projectile.location,
                smokeVel,
                MathUtils.getRandomNumberInRange(10f, 20f),
                1.5f,
                0.1f,
                0.3f,
                MathUtils.getRandomNumberInRange(2f, 2.5f),
                Color(200, 200, 200, 30),
                true
            )
        }
        for (i in 0 until 20) {
            val arcPoint = MathUtils.getRandomNumberInRange(projectile.facing - 5f, projectile.facing + 5f)
            val velocity = MathUtils.getPointOnCircumference(
                weapon.ship.velocity,
                MathUtils.getRandomNumberInRange(0f, 10f),
                MathUtils.getRandomNumberInRange(projectile.facing - 90f, projectile.facing + 90f)
            )
            var spawnLocation = MathUtils.getPointOnCircumference(
                projectile.location,
                MathUtils.getRandomNumberInRange(0f, 70f),
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
        engine.spawnExplosion(
            projectile.location,
            weapon.ship.velocity,
            effectCol.brighter(),
            30f,
            0.2f
        )
    }
}
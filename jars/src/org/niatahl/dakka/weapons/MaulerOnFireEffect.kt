package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnFireEffectPlugin
import com.fs.starfarer.api.combat.WeaponAPI
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class MaulerOnFireEffect : OnFireEffectPlugin {
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        val effectCol = Color(255,100,70,100)
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
                Color(50, 48, 45, 120),
                true
            )
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
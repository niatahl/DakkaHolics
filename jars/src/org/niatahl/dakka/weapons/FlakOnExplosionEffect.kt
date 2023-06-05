package org.niatahl.dakka.weapons

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.ProximityExplosionEffect
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.api.util.Misc.ZERO
import org.lazywizard.lazylib.MathUtils
import org.magiclib.kotlin.modify
import java.awt.Color

class FlakOnExplosionEffect : ProximityExplosionEffect {
    override fun onExplosion(explosion: DamagingProjectileAPI, originalProjectile: DamagingProjectileAPI) {
        val engine = Global.getCombatEngine()
        val effectCol = originalProjectile.projectileSpec.fringeColor.modify(alpha = 100)
        engine.addHitParticle(
            explosion.location,
            ZERO,
            explosion.collisionRadius * 3f,
            0.8f,
            0.05f,
            effectCol
        )
        engine.addSmoothParticle(
            explosion.location,
            ZERO,
            explosion.collisionRadius * 4f,
            0.8f,
            0.1f,
            effectCol
        )
        engine.spawnExplosion(explosion.location, ZERO, effectCol.darker(), explosion.collisionRadius * 2f, 0.15f)
        for (i in 0..9) {
            engine.addNebulaParticle(
                explosion.location,
                MathUtils.getRandomPointInCircle(ZERO, 15f),
                MathUtils.getRandomNumberInRange(explosion.collisionRadius * 0.5f, explosion.collisionRadius * 1f),
                2f,
                0f,
                0.3f,
                MathUtils.getRandomNumberInRange(1.5f, 3f),
                Color(22, 21, 20, 140),
                true
            )
        }
    }
}
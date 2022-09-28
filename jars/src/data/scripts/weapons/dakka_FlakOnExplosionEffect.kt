package data.scripts.weapons

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.ProximityExplosionEffect
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import java.awt.Color

class dakka_FlakOnExplosionEffect : ProximityExplosionEffect {
    override fun onExplosion(explosion: DamagingProjectileAPI, originalProjectile: DamagingProjectileAPI) {
        val engine = Global.getCombatEngine()
        val effectCol = Color(
                originalProjectile.projectileSpec.fringeColor.red,
                originalProjectile.projectileSpec.fringeColor.green,
                originalProjectile.projectileSpec.fringeColor.blue,
                70
        )
        engine.addHitParticle(
                explosion.location,
                Misc.ZERO,
                explosion.collisionRadius * 2f,
                0.8f,
                0.1f,
                effectCol
        )
        engine.addSmoothParticle(
                explosion.location,
                Misc.ZERO,
                explosion.collisionRadius * 3f,
                0.8f,
                0.1f,
                effectCol
        )
        for (i in 0..9) {
            engine.addNebulaParticle(
                    explosion.location,
                    MathUtils.getRandomPointInCircle(Misc.ZERO, 15f),
                    MathUtils.getRandomNumberInRange(explosion.collisionRadius * 0.5f, explosion.collisionRadius * 2f),
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
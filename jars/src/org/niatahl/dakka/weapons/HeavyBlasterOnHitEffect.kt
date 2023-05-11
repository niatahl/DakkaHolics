package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.CombatEntityAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnHitEffectPlugin
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class HeavyBlasterOnHitEffect : OnHitEffectPlugin {
    override fun onHit(projectile: DamagingProjectileAPI, target: CombatEntityAPI, point: Vector2f, shieldHit: Boolean, damageResult: ApplyDamageResultAPI, engine: CombatEngineAPI) {
        val effectCol = Color(
            projectile.projectileSpec.fringeColor.red,
            projectile.projectileSpec.fringeColor.green,
            projectile.projectileSpec.fringeColor.blue,
            100
        )

        engine.addHitParticle(
            projectile.location,
            Misc.ZERO,
            120f,
            0.8f,
            0.1f,
            effectCol
        )
        engine.addSmoothParticle(
            projectile.location,
            Misc.ZERO,
            200f,
            0.8f,
            0.1f,
            effectCol
        )
        for (i in 0..2) {
            engine.addNebulaParticle(
                projectile.location,
                MathUtils.getRandomPointInCircle(Misc.ZERO, 15f),
                MathUtils.getRandomNumberInRange(30f, 60f),
                2f,
                0f,
                0.3f,
                MathUtils.getRandomNumberInRange(1.5f, 3f),
                Color(197, 239, 246, 40),
                true
            )
        }
    }
}
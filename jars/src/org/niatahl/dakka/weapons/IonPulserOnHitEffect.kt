package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.CombatEntityAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI
import com.fs.starfarer.api.impl.combat.IonCannonOnHitEffect
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class IonPulserOnHitEffect : IonCannonOnHitEffect() {
    override fun onHit(
        projectile: DamagingProjectileAPI,
        target: CombatEntityAPI,
        point: Vector2f,
        shieldHit: Boolean,
        damageResult: ApplyDamageResultAPI,
        engine: CombatEngineAPI
    ) {
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
        engine.addSwirlyNebulaParticle(
            projectile.location,
            target.velocity,
            15f,
            2f,
            0.1f,
            0.2f,
            0.4f,
            effectCol,
            true
        )
        engine.addNebulaParticle(
            projectile.location,
            target.velocity,
            MathUtils.getRandomNumberInRange(20f, 30f),
            2f,
            0f,
            0.3f,
            MathUtils.getRandomNumberInRange(1f, 2f),
            Color(197, 239, 246, 40),
            true
        )

        super.onHit(projectile, target, point, shieldHit, damageResult, engine)
    }
}
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

class TPCOnHitEffect : OnHitEffectPlugin {
    override fun onHit(projectile: DamagingProjectileAPI, target: CombatEntityAPI?, point: Vector2f, shieldHit: Boolean, damageResult: ApplyDamageResultAPI, engine: CombatEngineAPI) {
        val effectCol = Color(
            projectile.projectileSpec.fringeColor.red,
            projectile.projectileSpec.fringeColor.green,
            projectile.projectileSpec.fringeColor.blue,
            100
        )
        engine.addNebulaParticle(
                projectile.location,
            Misc.ZERO,
            MathUtils.getRandomNumberInRange(60f, 80f),
                1.2f,
                0.1f,
                0.3f,
            MathUtils.getRandomNumberInRange(0.6f, 1.6f),
            Color(150, 150, 150, 100),
                true
        )
        engine.spawnExplosion(
                projectile.location,
            Misc.ZERO,
                effectCol.brighter(),
                40f,
                0.3f
        )
    }
}
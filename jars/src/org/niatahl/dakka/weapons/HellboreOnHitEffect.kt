package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.CombatEntityAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnHitEffectPlugin
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class HellboreOnHitEffect : OnHitEffectPlugin {
    override fun onHit(projectile: DamagingProjectileAPI, target: CombatEntityAPI, point: Vector2f, shieldHit: Boolean, damageResult: ApplyDamageResultAPI, engine: CombatEngineAPI) {
        val effectCol = Color(-0x32259ef5, true)
        engine.addHitParticle(
            projectile.location,
            Misc.ZERO,
            300f,
            1f,
            0.1f,
            effectCol
        )
        engine.spawnExplosion(
            projectile.location,
            Misc.ZERO,
            effectCol,
            120f,
            0.6f
        )
    }
}
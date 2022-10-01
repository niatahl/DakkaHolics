package data.scripts.weapons

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.CombatEntityAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnHitEffectPlugin
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI
import com.fs.starfarer.api.util.Misc
import data.scripts.util.MagicRender
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class dakka_ReaperOnHitEffect : OnHitEffectPlugin {
    override fun onHit(projectile: DamagingProjectileAPI, target: CombatEntityAPI, point: Vector2f, shieldHit: Boolean, damageResult: ApplyDamageResultAPI, engine: CombatEngineAPI) {
        engine.spawnExplosion(point, Misc.ZERO, PARTICLE_COLOR, 300f, 1.3f)
        engine.spawnExplosion(point, Misc.ZERO, CORE_COLOR, 150f, 1f)
        engine.addSmoothParticle(point, Misc.ZERO, 1000f, 1f, 0.1f, FLASH_COLOR)
        engine.addSmoothParticle(point, Misc.ZERO, 1300f, 1f, 0.2f, FLASH_COLOR)
        engine.addSmoothParticle(point, Misc.ZERO, 400f, 0.5f, 0.1f, PARTICLE_COLOR)
        engine.addHitParticle(point, Misc.ZERO, 200f, 0.5f, 0.25f, FLASH_COLOR)
        for (x in 0 until NUM_PARTICLES) {
            engine.addHitParticle(point,
                    MathUtils.getPointOnCircumference(null, MathUtils.getRandomNumberInRange(100f, 500f), Math.random().toFloat() * 360f),
                    10f, 1f, MathUtils.getRandomNumberInRange(0.3f, 0.6f), PARTICLE_COLOR)
        }
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx", "dakka_blastwave"),
                point,
                Misc.ZERO,
                Vector2f(100f, 100f),   // initial size
                Vector2f(1000f, 1000f),  // expansion
                360 * Math.random().toFloat(),
                0f,
                Color(232, 104, 104, 100),
                true,
                0f,
                0.1f,
                0.8f
        )
        Global.getSoundPlayer().playSound("dakka_reaper_boom", 1f, 1f, point, Misc.ZERO)
    }

    companion object {
        private val PARTICLE_COLOR = Color(255, 21, 21, 150)
        private val CORE_COLOR = Color(255, 34, 34)
        private val FLASH_COLOR = Color(255, 175, 175)
        private const val NUM_PARTICLES = 50
    }
}
package org.niatahl.dakka.weapons

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.CombatEntityAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnHitEffectPlugin
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI
import com.fs.starfarer.api.util.Misc
import org.magiclib.util.MagicRender
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class ReaperOnHitEffect : OnHitEffectPlugin {
    override fun onHit(projectile: DamagingProjectileAPI, target: CombatEntityAPI, point: Vector2f, shieldHit: Boolean, damageResult: ApplyDamageResultAPI, engine: CombatEngineAPI) {
        engine.spawnExplosion(point, Misc.ZERO, PARTICLE_COLOR, 500f, 1.2f)
        engine.spawnExplosion(point, Misc.ZERO, CORE_COLOR, 300f, 0.8f)
        engine.addSmoothParticle(point, Misc.ZERO, 1000f, 1f, 0.1f, FLASH_COLOR)
        engine.addSmoothParticle(point, Misc.ZERO, 1300f, 1f, 0.2f, FLASH_COLOR)
        engine.addSmoothParticle(point, Misc.ZERO, 400f, 0.5f, 0.1f, PARTICLE_COLOR)
        engine.addHitParticle(point, Misc.ZERO, 200f, 0.5f, 0.25f, FLASH_COLOR)
        for (x in 0 until NUM_PARTICLES) {
            engine.addHitParticle(point,
                MathUtils.getPointOnCircumference(
                    null,
                    MathUtils.getRandomNumberInRange(100f, 500f),
                    Math.random().toFloat() * 360f
                ),
                    10f, 1f, MathUtils.getRandomNumberInRange(0.3f, 0.6f), PARTICLE_COLOR)
            engine.addNebulaParticle(
                MathUtils.getRandomPointInCircle(point, 70f),
                Misc.ZERO,
                MathUtils.getRandomNumberInRange(50f, 100f),
                1.3f,
                0.1f,
                0.25f,
                MathUtils.getRandomNumberInRange(3f, 6f),
                Color(50, 50, 50, 100)
            )
        }
        MagicRender.battlespace(
            Global.getSettings().getSprite("fx", "dakka_blastwave"),
            point,
            Misc.ZERO,
            Vector2f(100f, 100f),   // initial size
            Vector2f(800f, 800f),  // expansion
            360 * Math.random().toFloat(),
            0f,
            Color(255, 50, 50, 120),
            true,
            0f,
            0.1f,
            0.8f
        )
        MagicRender.battlespace(
            Global.getSettings().getSprite("fx", "dakka_blastcloud"),
            point,
            Misc.ZERO,
            Vector2f(90f, 90f),   // initial size
            Vector2f(700f, 700f),  // expansion
            360 * Math.random().toFloat(),
            0f,
            Color(255, 100, 20, 60),
            true,
            0f,
            0.1f,
            1f
        )
        MagicRender.battlespace(
            Global.getSettings().getSprite("fx", "dakka_blastcloud"),
            point,
            Misc.ZERO,
            Vector2f(90f, 90f),   // initial size
            Vector2f(400f, 400f),  // expansion
            360 * Math.random().toFloat(),
            -20f,
            Color(255, 80, 20, 60),
            true,
            0f,
            0.1f,
            1f
        )
        MagicRender.battlespace(
            Global.getSettings().getSprite("fx", "dakka_blastcloud"),
            point,
            Misc.ZERO,
            Vector2f(90f, 90f),   // initial size
            Vector2f(550f, 550f),  // expansion
            360 * Math.random().toFloat(),
            20f,
            Color(255, 60, 20, 60),
            true,
            0f,
            0.1f,
            1f
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
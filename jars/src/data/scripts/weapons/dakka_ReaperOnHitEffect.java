package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static com.fs.starfarer.api.util.Misc.ZERO;

public class dakka_ReaperOnHitEffect implements OnHitEffectPlugin {

    private static final Color PARTICLE_COLOR = new Color(255, 21, 21, 150);
    private static final Color CORE_COLOR = new Color(255, 34, 34);
    private static final Color FLASH_COLOR = new Color(255, 175, 175);
    private static final int NUM_PARTICLES = 50;

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        engine.spawnExplosion(point, ZERO, PARTICLE_COLOR, 300f, 1.3f);
        engine.spawnExplosion(point, ZERO, CORE_COLOR, 150f, 1f);
        engine.addSmoothParticle(point, ZERO, 1000, 1f, 0.1f, FLASH_COLOR);
        engine.addSmoothParticle(point, ZERO, 1300, 1f, 0.2f, FLASH_COLOR);

        engine.addSmoothParticle(point, ZERO, 400f, 0.5f, 0.1f, PARTICLE_COLOR);
        engine.addHitParticle(point, ZERO, 200f, 0.5f, 0.25f, FLASH_COLOR);
        for (int x = 0; x < NUM_PARTICLES; x++) {
            engine.addHitParticle(point,
                    MathUtils.getPointOnCircumference(null, MathUtils.getRandomNumberInRange(100f, 500f), (float) Math.random() * 360f),
                    10f, 1f, MathUtils.getRandomNumberInRange(0.3f, 0.6f), PARTICLE_COLOR);
        }
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","dakka_blastwave"),
                point,
                ZERO,
                new Vector2f(100,100),
                new Vector2f(1000,1000),
                //angle,
                360*(float)Math.random(),
                0,
                new Color(232, 104, 104,100),
                true,
                0,
                0.1f,
                0.8f
        );
        Global.getSoundPlayer().playSound("dakka_reaper_boom",1f,1f,point,ZERO);
    }
}

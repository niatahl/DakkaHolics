package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class dakka_TPCOnHitEffect implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        Color effectCol = new Color(
                projectile.getProjectileSpec().getFringeColor().getRed(),
                projectile.getProjectileSpec().getFringeColor().getGreen(),
                projectile.getProjectileSpec().getFringeColor().getBlue(),
                100
        );
        engine.addNebulaParticle(
                projectile.getLocation(),
                Misc.ZERO,
                MathUtils.getRandomNumberInRange(60f, 80f),
                1.2f,
                0.1f,
                0.3f,
                MathUtils.getRandomNumberInRange(0.6f, 1.6f),
                new Color(150,150,150,100),
                true
        );

        engine.spawnExplosion(
                projectile.getLocation(),
                Misc.ZERO,
                effectCol.brighter(),
                40f,
                0.3f
        );
    }
}

package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ProximityExplosionEffect;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class dakka_FlakOnExplosionEffect implements ProximityExplosionEffect {
    @Override
    public void onExplosion(DamagingProjectileAPI explosion, DamagingProjectileAPI originalProjectile) {
        CombatEngineAPI engine = Global.getCombatEngine();

        engine.addHitParticle(
                originalProjectile.getLocation(),
                Misc.ZERO,
                100f,
                1f,
                0.1f,
                originalProjectile.getProjectileSpec().getCoreColor()
        );

        for (int i = 0; i < 10; i++) {
            engine.addNebulaParticle(
                    originalProjectile.getLocation(),
                    MathUtils.getRandomPointInCircle(Misc.ZERO,30f),
                    MathUtils.getRandomNumberInRange(25f, 50f),
                    1.5f,
                    0.1f,
                    0.3f,
                    MathUtils.getRandomNumberInRange(2f, 2.5f),
                    new Color(50, 48, 45, 120),
                    true
            );
        }
    }
}

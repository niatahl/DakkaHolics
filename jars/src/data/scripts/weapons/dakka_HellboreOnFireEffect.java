package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class dakka_HellboreOnFireEffect implements OnFireEffectPlugin {
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        Color effectCol =new Color(0xA0DA610B, true);

        engine.addHitParticle(
                projectile.getLocation(),
                weapon.getShip().getVelocity(),
                120f,
                1f,
                0.1f,
                effectCol.brighter()
        );

        Vector2f projVel;
        Vector2f smokeVel;

        for (int i = 0; i < 10; i++) {
            projVel = new Vector2f();
            smokeVel = new Vector2f();
            projectile.getVelocity().normalise(projVel);
            projVel.scale(MathUtils.getRandomNumberInRange(0f,30f));
            Vector2f.add(weapon.getShip().getVelocity(),projVel,smokeVel);

            engine.addNebulaParticle(
                    projectile.getLocation(),
                    smokeVel,
                    MathUtils.getRandomNumberInRange(40f, 60f),
                    1.5f,
                    0.1f,
                    0.3f,
                    MathUtils.getRandomNumberInRange(2f, 2.5f),
                    new Color(50, 48, 45, 120),
                    true
            );
        }

        engine.spawnExplosion(
                projectile.getLocation(),
                weapon.getShip().getVelocity(),
                effectCol.brighter(),
                50f,
                0.2f
        );
    }
}

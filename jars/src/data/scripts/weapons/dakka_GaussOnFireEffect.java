package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class dakka_GaussOnFireEffect implements OnFireEffectPlugin {

    static final int NUM_PARTICLES = 100;

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

        engine.spawnExplosion(projectile.getLocation(), weapon.getShip().getVelocity(), new Color(154, 211,255,120), 20f, 0.15f);
        for (int i = 0; i < NUM_PARTICLES; i++) {
            float arcPoint = MathUtils.getRandomNumberInRange(projectile.getFacing()-5f, projectile.getFacing()+5f);
            Vector2f velocity = MathUtils.getPointOnCircumference(weapon.getShip().getVelocity(), MathUtils.getRandomNumberInRange(0f, 10f), MathUtils.getRandomNumberInRange(projectile.getFacing()-90f, projectile.getFacing()+90f));
            Vector2f spawnLocation = MathUtils.getPointOnCircumference(projectile.getLocation(), MathUtils.getRandomNumberInRange(0f, 110f), arcPoint);
            spawnLocation = MathUtils.getRandomPointInCircle(spawnLocation,MathUtils.getRandomNumberInRange(0f,5f));
            engine.addHitParticle(spawnLocation, velocity, MathUtils.getRandomNumberInRange(2f,3f), 20f, MathUtils.getRandomNumberInRange(0.5f,3f),new Color(151, 180, 255,255));
            //engine.addSmoothParticle(spawnLocation, velocity, MathUtils.getRandomNumberInRange(2f,4f), 15f, MathUtils.getRandomNumberInRange(0.5f,3f),new Color(151, 180, 255,255));
        }
    }
}

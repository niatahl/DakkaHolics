package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;

public class dakka_TPCOnFireEffect implements OnFireEffectPlugin {
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

        Color effectCol = new Color(
                projectile.getProjectileSpec().getFringeColor().getRed(),
                projectile.getProjectileSpec().getFringeColor().getGreen(),
                projectile.getProjectileSpec().getFringeColor().getBlue(),
                100
        );

        engine.addNebulaParticle(
                projectile.getLocation(),
                weapon.getShip().getVelocity(),
                MathUtils.getRandomNumberInRange(40f, 60f),
                1.2f,
                0.1f,
                0.3f,
                MathUtils.getRandomNumberInRange(0.6f, 1.6f),
                new Color(150,150,150,100),
                true
        );

        engine.addHitParticle(
                projectile.getLocation(),
                Misc.ZERO,
                200f,
                0.7f,
                0.1f,
                effectCol.brighter()
        );

        engine.spawnExplosion(
                projectile.getLocation(),
                weapon.getShip().getVelocity(),
                effectCol.brighter(),
                30f,
                0.15f
        );

    }
}

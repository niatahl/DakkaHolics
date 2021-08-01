package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class dakka_MjolnirOnFireEffect implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {

    private static final float ANIMATION_TIME = 0.5f;
    private float timeLeft = 0f;
    private int currentBarrel = 1;

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        Vector2f point = projectile.getLocation();
        engine.spawnExplosion(point, weapon.getShip().getVelocity(), new Color(169, 154,255,120), 60f, 0.15f);
        timeLeft = ANIMATION_TIME / weapon.getShip().getMutableStats().getBallisticRoFMult().getModifiedValue();
        currentBarrel = (currentBarrel == 0) ? 1 : 0;
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (timeLeft > 0f) {

            timeLeft -= amount;

            //Muzzle location calculation
            Vector2f point = new Vector2f();

            if (weapon.getSlot().isHardpoint()) {
                point.x = weapon.getSpec().getHardpointFireOffsets().get(currentBarrel).x;
                point.y = weapon.getSpec().getHardpointFireOffsets().get(currentBarrel).y;
            } else if (weapon.getSlot().isTurret()) {
                point.x = weapon.getSpec().getTurretFireOffsets().get(currentBarrel).x;
                point.y = weapon.getSpec().getTurretFireOffsets().get(currentBarrel).y;
            } else {
                point.x = weapon.getSpec().getHiddenFireOffsets().get(currentBarrel).x;
                point.y = weapon.getSpec().getHiddenFireOffsets().get(currentBarrel).y;
            }

            point = VectorUtils.rotate(point, weapon.getCurrAngle(), new Vector2f(0f, 0f));
            point.x += weapon.getLocation().x;
            point.y += weapon.getLocation().y;

            Vector2f offset = new Vector2f(5,0);
            offset = VectorUtils.rotate(offset,weapon.getCurrAngle(), new Vector2f(0f,0f));
            Vector2f.add(point,offset,point);

            int alpha = Math.min(255, Math.max(0, (int) (230f-(220f/ANIMATION_TIME*(ANIMATION_TIME-timeLeft)))));

            MagicRender.singleframe(
                    Global.getSettings().getSprite("fx","dakka_shockwave"),
                    point,
                    new Vector2f(9f+((60f/ANIMATION_TIME)*(ANIMATION_TIME-timeLeft)),3f+((20f/ANIMATION_TIME)*(ANIMATION_TIME-timeLeft))),
                    weapon.getCurrAngle()+90f,
                    new Color(200, 210,255, alpha),
                    true
            );
        }
    }
}

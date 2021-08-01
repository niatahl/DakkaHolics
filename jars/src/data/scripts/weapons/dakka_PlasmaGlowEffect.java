package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.sun.scenario.Settings;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class dakka_PlasmaGlowEffect implements EveryFrameWeaponEffectPlugin, OnFireEffectPlugin {

    private final List<DamagingProjectileAPI> registeredProjectiles = new ArrayList<>();

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        registeredProjectiles.add(projectile);
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (weapon.getSlot().getWeaponType() == WeaponAPI.WeaponType.ENERGY)  {

        }

        List<DamagingProjectileAPI> toRemove = new ArrayList<>();

        for (DamagingProjectileAPI proj : registeredProjectiles) {
            //Ignore already-collided projectiles, and projectiles that don't match our IDs
            if (proj.getProjectileSpecId() == null || proj.didDamage() || !engine.isEntityInPlay(proj)) {
                toRemove.add(proj);
                continue;
            }

            MagicRender.singleframe(
                    Global.getSettings().getSprite("fx","tahlan_dakka_glow"),
                    proj.getLocation(),
                    new Vector2f(200f,200f),
                    proj.getFacing(),
                    new Color(246, 62, 122, 54),
                    true
            );
        }

        for (DamagingProjectileAPI proj : toRemove) {
            registeredProjectiles.remove(proj);
        }
    }



}

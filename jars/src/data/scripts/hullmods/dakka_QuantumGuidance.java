package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.scripts.weapons.dakka_ProjectileGuidancePlugin;
import org.lazywizard.lazylib.combat.CombatUtils;

import java.util.ArrayList;
import java.util.List;

public class dakka_QuantumGuidance extends BaseHullMod {

    private final List<DamagingProjectileAPI> alreadyRegisteredProjectiles = new ArrayList<DamagingProjectileAPI>();

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        if (index == 0) return "ballistic";
        if (index == 1) return "energy";
        return null;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        ShipAPI target = null;
        CombatEngineAPI engine = Global.getCombatEngine();

        for (DamagingProjectileAPI proj : CombatUtils.getProjectilesWithinRange(ship.getLocation(), ship.getCollisionRadius()+100f)) {
            if (!alreadyRegisteredProjectiles.contains(proj) && engine.isEntityInPlay(proj) && !proj.didDamage()) {
                alreadyRegisteredProjectiles.add(proj);
                WeaponAPI weapon = proj.getWeapon();
                if (weapon == null)
                    continue;

                if ( !(weapon.getType() == WeaponAPI.WeaponType.ENERGY || weapon.getType() == WeaponAPI.WeaponType.BALLISTIC) )
                    continue;

                if (ship.getWeaponGroupFor(weapon) != null) {
                    //WEAPON IN AUTOFIRE
                    if (ship.getWeaponGroupFor(weapon).isAutofiring()  //weapon group is autofiring
                            && ship.getSelectedGroupAPI() != ship.getWeaponGroupFor(weapon)) { //weapon group is not the selected group
                        target = ship.getWeaponGroupFor(weapon).getAutofirePlugin(weapon).getTargetShip();
                    } else {
                        target = ship.getShipTarget();
                    }
                }

                engine.addPlugin(new dakka_ProjectileGuidancePlugin(proj, target));

            }
        }

        //And clean up our registered projectile list
        List<DamagingProjectileAPI> cloneList = new ArrayList<>(alreadyRegisteredProjectiles);
        for (DamagingProjectileAPI proj : cloneList) {
            if (!engine.isEntityInPlay(proj) || proj.didDamage()) {
                alreadyRegisteredProjectiles.remove(proj);
            }
        }
    }
}

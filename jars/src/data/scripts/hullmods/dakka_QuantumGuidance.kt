package data.scripts.hullmods

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipAPI.HullSize
import com.fs.starfarer.api.combat.WeaponAPI
import data.scripts.weapons.dakka_ProjectileGuidancePlugin
import org.lazywizard.lazylib.combat.CombatUtils

class dakka_QuantumGuidance : BaseHullMod() {
    private val alreadyRegisteredProjectiles: MutableList<DamagingProjectileAPI> = ArrayList()
    override fun getDescriptionParam(index: Int, hullSize: HullSize, ship: ShipAPI): String? {
        if (index == 0) return "ballistic"
        return if (index == 1) "energy" else null
    }

    override fun advanceInCombat(ship: ShipAPI, amount: Float) {
        var target: ShipAPI? = null
        val engine = Global.getCombatEngine()
        for (proj in CombatUtils.getProjectilesWithinRange(ship.location, ship.collisionRadius + 100f)) {
            if (!alreadyRegisteredProjectiles.contains(proj) && engine.isEntityInPlay(proj) && !proj.didDamage()) {
                alreadyRegisteredProjectiles.add(proj)
                val weapon = proj.weapon ?: continue
                if (!(weapon.type == WeaponAPI.WeaponType.ENERGY || weapon.type == WeaponAPI.WeaponType.BALLISTIC)) continue
                if (ship.getWeaponGroupFor(weapon) != null) {
                    //WEAPON IN AUTOFIRE
                    target = if (ship.getWeaponGroupFor(weapon).isAutofiring //weapon group is autofiring
                            && ship.selectedGroupAPI !== ship.getWeaponGroupFor(weapon)) { //weapon group is not the selected group
                        ship.getWeaponGroupFor(weapon).getAutofirePlugin(weapon).targetShip
                    } else {
                        ship.shipTarget
                    }
                }
                engine.addPlugin(dakka_ProjectileGuidancePlugin(proj, target))
            }
        }

        //And clean up our registered projectile list
        val cloneList: List<DamagingProjectileAPI> = ArrayList(alreadyRegisteredProjectiles)
        for (proj in cloneList) {
            if (!engine.isEntityInPlay(proj) || proj.didDamage()) {
                alreadyRegisteredProjectiles.remove(proj)
            }
        }
    }
}
package org.niatahl.dakka.hullmods

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipAPI.HullSize
import com.fs.starfarer.api.combat.WeaponAPI
import org.niatahl.dakka.weapons.ProjectileGuidancePlugin
import org.lazywizard.lazylib.combat.CombatUtils

class QuantumGuidance : BaseHullMod() {
    private val alreadyRegisteredProjectiles: MutableList<DamagingProjectileAPI> = ArrayList()
    override fun getDescriptionParam(index: Int, hullSize: HullSize?, ship: ShipAPI?): String? {
        if (index == 0) return "ballistic"
        return if (index == 1) "energy" else null
    }

    override fun advanceInCombat(ship: ShipAPI, amount: Float) {
        var target: ShipAPI?
        val engine = Global.getCombatEngine()
        for (proj in CombatUtils.getProjectilesWithinRange(ship.location, ship.collisionRadius + 100f)) {
            if (!alreadyRegisteredProjectiles.contains(proj) && engine.isEntityInPlay(proj) && !proj.didDamage()) {
                alreadyRegisteredProjectiles.add(proj)
                val weapon = proj.weapon ?: continue
                if (!(weapon.type == WeaponAPI.WeaponType.ENERGY || weapon.type == WeaponAPI.WeaponType.BALLISTIC)) continue
                val group = ship.getWeaponGroupFor(weapon)
                target = if (group != null && group.isAutofiring && group !== ship.selectedGroupAPI) group.getAutofirePlugin(weapon).targetShip else ship.shipTarget
                engine.addPlugin(ProjectileGuidancePlugin(proj, target))
            }
        }

        val cloneList: List<DamagingProjectileAPI> = ArrayList(alreadyRegisteredProjectiles)

        cloneList.forEach { proj ->
            if (!engine.isEntityInPlay(proj) || proj.didDamage()) {
                alreadyRegisteredProjectiles.remove(proj)
            }
        }
    }
}
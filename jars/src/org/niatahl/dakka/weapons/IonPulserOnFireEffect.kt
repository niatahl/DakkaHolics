package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnFireEffectPlugin
import com.fs.starfarer.api.combat.WeaponAPI

class IonPulserOnFireEffect : OnFireEffectPlugin {
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        val flashCol = projectile.projectileSpec.fringeColor
        engine.addSwirlyNebulaParticle(
            projectile.location,
            weapon.ship.velocity,
            25f,
            2f,
            0.1f,
            0.2f,
            0.4f,
            flashCol,
            true
        )
    }
}
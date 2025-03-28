package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnFireEffectPlugin
import com.fs.starfarer.api.combat.WeaponAPI
import java.awt.Color

class HeavyBlasterOnFireEffect : OnFireEffectPlugin {
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        val flashCol = projectile.projectileSpec.fringeColor
        val steamCol = Color(220, 255, 255, 100)
        for (i in 1..3)
            engine.addSwirlyNebulaParticle(
                projectile.location,
                weapon.ship.velocity,
                22f * i / 2,
                1.5f,
                0.1f,
                0.2f,
                0.6f,
                flashCol,
                true
            )
        engine.addNebulaParticle(projectile.location, weapon.ship.velocity, 50f, 1.5f, 0.1f, 0.2f, 1f, steamCol)
    }
}
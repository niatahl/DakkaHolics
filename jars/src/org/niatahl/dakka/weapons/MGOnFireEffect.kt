package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnFireEffectPlugin
import com.fs.starfarer.api.combat.WeaponAPI
import org.magiclib.kotlin.modify
import java.awt.Color

class MGOnFireEffect : OnFireEffectPlugin {
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        val steamCol = Color(150,150,150,20)
        engine.addNebulaParticle(projectile.location,weapon.ship.velocity,weapon.muzzleFlashSpec.particleSizeRange,1.5f, 0.1f,0.2f,1f,steamCol)
//        engine.spawnExplosion(projectile.location, weapon.ship.velocity, weapon.muzzleFlashSpec.particleColor.modify(alpha = 30), weapon.muzzleFlashSpec.particleSizeRange, 0.15f)
        engine.addSmoothParticle(
            projectile.location,
            weapon.ship.velocity,
            weapon.muzzleFlashSpec.particleSizeRange * 4f,
            1f,
            0.05f,
            weapon.muzzleFlashSpec.particleColor.modify(alpha = 120)
        )
    }
}
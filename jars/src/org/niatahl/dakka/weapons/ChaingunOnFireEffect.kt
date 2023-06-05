package org.niatahl.dakka.weapons

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import org.lazywizard.lazylib.MathUtils
import org.magiclib.util.MagicRender
import org.lazywizard.lazylib.VectorUtils
import org.lwjgl.util.vector.Vector2f
import org.magiclib.kotlin.modify
import java.awt.Color

class ChaingunOnFireEffect : OnFireEffectPlugin {

    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        engine.spawnExplosion(projectile.location, weapon.ship.velocity, weapon.muzzleFlashSpec.particleColor.modify(alpha = 100), weapon.muzzleFlashSpec.particleSizeRange, 0.15f)
        val steamCol = Color(150,150,150,20)
        engine.addNebulaParticle(projectile.location,weapon.ship.velocity,weapon.muzzleFlashSpec.particleSizeRange,1.5f, 0.1f,0.2f,1f,steamCol)

    }
}
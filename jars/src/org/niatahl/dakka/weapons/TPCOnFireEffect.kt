package org.niatahl.dakka.weapons

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.OnFireEffectPlugin
import com.fs.starfarer.api.combat.WeaponAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.magiclib.kotlin.modify
import java.awt.Color

class TPCOnFireEffect : OnFireEffectPlugin {
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        val effectCol = Color(
            projectile.projectileSpec.fringeColor.red,
            projectile.projectileSpec.fringeColor.green,
            projectile.projectileSpec.fringeColor.blue,
            100
        )
        engine.addNebulaParticle(
            projectile.location,
            weapon.ship.velocity,
            MathUtils.getRandomNumberInRange(40f, 60f),
            1.2f,
            0.1f,
            0.3f,
            MathUtils.getRandomNumberInRange(0.6f, 1.6f),
            Color(150, 150, 150, 100),
            true
        )
        engine.addSmoothParticle(
            projectile.location,
            Misc.ZERO,
            200f,
            0.7f,
            0.1f,
            effectCol.brighter()
        )
        engine.spawnExplosion(
            projectile.location,
            weapon.ship.velocity,
            effectCol.modify(alpha = 50),
            30f,
            0.15f
        )
    }
}
package org.niatahl.dakka.weapons

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import org.magiclib.util.MagicRender
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class PlasmaGlowEffect : EveryFrameWeaponEffectPlugin, OnFireEffectPlugin {
    private val registeredProjectiles: MutableList<DamagingProjectileAPI> = ArrayList()
    override fun onFire(projectile: DamagingProjectileAPI, weapon: WeaponAPI, engine: CombatEngineAPI) {
        registeredProjectiles.add(projectile)
    }

    override fun advance(amount: Float, engine: CombatEngineAPI, weapon: WeaponAPI) {
        val toRemove: MutableList<DamagingProjectileAPI> = ArrayList()
        for (proj in registeredProjectiles) {
            //Ignore already-collided projectiles, and projectiles that don't match our IDs
            if (proj.projectileSpecId == null || proj.didDamage() || !engine.isEntityInPlay(proj)) {
                toRemove.add(proj)
                continue
            }
            MagicRender.singleframe(
                Global.getSettings().getSprite("fx", "tahlan_dakka_glow"),
                proj.location,
                Vector2f(200f, 200f),
                proj.facing,
                Color(246, 62, 122, 54),
                true
            )
        }
        for (proj in toRemove) {
            registeredProjectiles.remove(proj)
        }
    }
}
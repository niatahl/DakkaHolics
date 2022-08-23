package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class dakka_HellboreOnHitEffect implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        Color effectCol = new Color(0xCDDA610B, true);

        engine.addHitParticle(
                projectile.getLocation(),
                Misc.ZERO,
                300f,
                1f,
                0.1f,
                effectCol
        );

        engine.spawnExplosion(
                projectile.getLocation(),
                Misc.ZERO,
                effectCol.brighter(),
                120f,
                0.2f
        );
    }
}

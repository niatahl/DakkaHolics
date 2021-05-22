package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import data.scripts.util.MagicRender;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dakka_ProjectileGlowPlugin extends BaseEveryFrameCombatPlugin {

    private static final Map<String, String> SPRITES = new HashMap<String, String>();
    static {
        SPRITES.put("plasma_shot", "tahlan_dakka_glow");
        SPRITES.put("autopulse_shot", "tahlan_dakka_glow");
    }

    private static final Map<String, Float> SIZES_X = new HashMap<String, Float>();
    static {
        SIZES_X.put("plasma_shot", 200f);
        SIZES_X.put("autopulse_shot", 100f);
    }

    private static final Map<String, Float> SIZES_Y = new HashMap<String, Float>();
    static {
        SIZES_Y.put("plasma_shot", 200f);
        SIZES_Y.put("autopulse_shot", 100f);
    }

    private static final Map<String, Color> COLORS = new HashMap<String, Color>();
    static {
        COLORS.put("plasma_shot", new Color(246, 62, 122, 54));
        COLORS.put("autopulse_shot", new Color(50, 50, 255, 20));
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (Global.getCombatEngine() == null) {
            return;
        }
        CombatEngineAPI engine = Global.getCombatEngine();

        //Runs once on each projectile that matches one of the IDs specified in our maps
        for (DamagingProjectileAPI proj : engine.getProjectiles()) {
            //Ignore already-collided projectiles, and projectiles that don't match our IDs
            if (proj.getProjectileSpecId() == null || proj.didDamage()) {
                continue;
            }

            if (!SPRITES.containsKey(proj.getProjectileSpecId())) {
                continue;
            }

            String specID = proj.getProjectileSpecId();
            MagicRender.singleframe(
                    Global.getSettings().getSprite("fx",SPRITES.get(specID)),
                    proj.getLocation(),
                    new Vector2f(SIZES_X.get(specID),SIZES_Y.get(specID)),
                    proj.getFacing(),
                    COLORS.get(specID),
                    true
            );

        }
    }
}

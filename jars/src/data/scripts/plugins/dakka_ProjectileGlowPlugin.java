package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import data.scripts.util.MagicRender;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data.scripts.util.MagicSettings.toColor4;

public class dakka_ProjectileGlowPlugin extends BaseEveryFrameCombatPlugin {

    private static final Logger LOG = Global.getLogger(dakka_ProjectileGlowPlugin.class);

    // HashMaps to make Nicke proud
    // Can reduce that to a single Map with lists of dataobjects at some point but cba right now

    private static final Map<String, String> SPRITES = new HashMap<String, String>();
    private static final Map<String, Float> SIZES_X = new HashMap<String, Float>();
    private static final Map<String, Float> SIZES_Y = new HashMap<String, Float>();
    private static final Map<String, Color> COLORS = new HashMap<String, Color>();


    @Override
    public void init(CombatEngineAPI engine) {
        if (Global.getSettings().isDevMode()) {
            getGlowData();
        }
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (Global.getCombatEngine() == null) {
            return;
        }

        CombatEngineAPI engine = Global.getCombatEngine();

        if (engine.isPaused()) {
            return;
        }

        //Runs once on each projectile that matches one of the IDs specified in our maps
        for (DamagingProjectileAPI proj : engine.getProjectiles()) {
            //Ignore already-collided projectiles, and projectiles that don't match our IDs
            if ((proj.getProjectileSpecId() == null) || !(proj.getBrightness() > 0f) || (proj.didDamage() && proj.isFading())) {
                continue;
            }

            if (!SPRITES.containsKey(proj.getProjectileSpecId())) {
                continue;
            }

            Color colorToUse;

            String specID = proj.getProjectileSpecId();

            if (proj.isFading()) {
                colorToUse = new Color(
                        COLORS.get(specID).getRed(),
                        COLORS.get(specID).getGreen(),
                        COLORS.get(specID).getBlue(),
                        Math.min(255, Math.max(0, Math.round(COLORS.get(specID).getAlpha() * proj.getBrightness()))));
            } else {
                colorToUse = COLORS.get(specID);
            }

            MagicRender.singleframe(
                    Global.getSettings().getSprite("glow_fx", SPRITES.get(specID)),
                    proj.getLocation(),
                    new Vector2f(SIZES_X.get(specID), SIZES_Y.get(specID)),
                    proj.getFacing() - 90f,
                    colorToUse,
                    true
            );

        }
    }

    public static void getGlowData() {
        SPRITES.clear();
        SIZES_X.clear();
        SIZES_Y.clear();
        COLORS.clear();

        JSONArray glowData = new JSONArray();
        try {
            glowData = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/config/dakka/dakka_GlowData.csv", "zzz_DakkaHolics");
        } catch (IOException | JSONException | RuntimeException ex) {
            LOG.error("unable to read Glow data");
            LOG.error(ex);
        }

        for (int i=0; i< glowData.length(); i++) {
            try {
                JSONObject row = glowData.getJSONObject(i);

                String glow_id = row.getString("id");
                if (SPRITES.containsKey(glow_id)) {
                    continue;
                }

                SPRITES.put(glow_id,row.getString("sprite"));
                SIZES_X.put(glow_id,(float)row.getDouble("width"));
                SIZES_Y.put(glow_id,(float)row.getDouble("length"));
                COLORS.put(glow_id,toColor4(row.getString("color")));

            } catch (JSONException ignored) {
            }

        }
    }

}

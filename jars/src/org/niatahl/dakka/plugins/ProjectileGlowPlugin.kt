package org.niatahl.dakka.plugins

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin
import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.input.InputEventAPI
import org.magiclib.util.MagicRender
import org.magiclib.util.MagicSettings
import org.json.JSONArray
import org.json.JSONException
import org.lwjgl.util.vector.Vector2f
import java.awt.Color
import java.io.IOException
import kotlin.math.roundToInt

class ProjectileGlowPlugin : BaseEveryFrameCombatPlugin() {
    override fun init(engine: CombatEngineAPI) {
        if (Global.getSettings().isDevMode) {
            glowData
        }
    }

    override fun advance(amount: Float, events: List<InputEventAPI>) {
        if (Global.getCombatEngine() == null) {
            return
        }
        val engine = Global.getCombatEngine()
        if (engine.isPaused) {
            return
        }

        //Runs once on each projectile that matches one of the IDs specified in our maps
        for (proj in engine.projectiles) {
            //Ignore already-collided projectiles, and projectiles that don't match our IDs
            if (proj.projectileSpecId == null || proj.brightness <= 0f || proj.didDamage() && proj.isFading) {
                continue
            }
            if (!SPRITES.containsKey(proj.projectileSpecId)) {
                continue
            }
            var colorToUse: Color?
            val specID = proj.projectileSpecId
            colorToUse = if (proj.isFading) {
                Color(
                    COLORS[specID]!!.red,
                    COLORS[specID]!!.green,
                    COLORS[specID]!!.blue,
                    (COLORS[specID]!!.alpha * proj.brightness).roundToInt().coerceIn(0, 255)
                )
            } else {
                COLORS[specID]
            }
            MagicRender.singleframe(
                Global.getSettings().getSprite("glow_fx", SPRITES[specID]),
                proj.location,
                Vector2f(SIZES_X[specID]!!, SIZES_Y[specID]!!),
                proj.facing - 90f,
                colorToUse,
                true
            )
        }
    }

    companion object {
        private val LOG = Global.getLogger(ProjectileGlowPlugin::class.java)

        // HashMaps to make Nicke proud
        // Can reduce that to a single Map with lists of dataobjects at some point but cba right now
        private val SPRITES: MutableMap<String, String> = HashMap()
        private val SIZES_X: MutableMap<String, Float> = HashMap()
        private val SIZES_Y: MutableMap<String, Float> = HashMap()
        private val COLORS: MutableMap<String, Color> = HashMap()
        @JvmStatic
        val glowData: Unit
            get() {
                SPRITES.clear()
                SIZES_X.clear()
                SIZES_Y.clear()
                COLORS.clear()
                var glowData = JSONArray()
                try {
                    glowData = Global.getSettings()
                        .getMergedSpreadsheetDataForMod("id", "data/config/dakka/dakka_GlowData.csv", "zzz_DakkaHolics")
                } catch (ex: IOException) {
                    LOG.error("unable to read Glow data")
                    LOG.error(ex)
                } catch (ex: JSONException) {
                    LOG.error("unable to read Glow data")
                    LOG.error(ex)
                } catch (ex: RuntimeException) {
                    LOG.error("unable to read Glow data")
                    LOG.error(ex)
                }
                for (i in 0 until glowData.length()) {
                    try {
                        val row = glowData.getJSONObject(i)
                        val glow_id = row.getString("id")
                        if (SPRITES.containsKey(glow_id)) {
                            continue
                        }
                        SPRITES[glow_id] = row.getString("sprite")
                        SIZES_X[glow_id] = row.getDouble("width").toFloat()
                        SIZES_Y[glow_id] = row.getDouble("length").toFloat()
                        COLORS[glow_id] = MagicSettings.toColor4(row.getString("color"))
                    } catch (ignored: JSONException) {
                    }
                }
            }
    }
}
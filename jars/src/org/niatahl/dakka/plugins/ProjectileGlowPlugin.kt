package org.niatahl.dakka.plugins

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin
import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.input.InputEventAPI
import org.magiclib.util.MagicRender
import org.magiclib.util.MagicSettings
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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
            if (!spriteMap.containsKey(proj.projectileSpecId)) {
                continue
            }
            var colorToUse: Color?
            val specID = proj.projectileSpecId
            colorToUse = if (proj.isFading) {
                Color(
                    colorMap[specID]!!.red,
                    colorMap[specID]!!.green,
                    colorMap[specID]!!.blue,
                    (colorMap[specID]!!.alpha * proj.brightness).roundToInt().coerceIn(0, 255)
                )
            } else {
                colorMap[specID]
            }
            MagicRender.singleframe(
                Global.getSettings().getSprite("glow_fx", spriteMap[specID]),
                proj.location,
                Vector2f(xMap[specID]!!, yMap[specID]!!),
                proj.facing - 90f,
                colorToUse,
                true
            )
        }
    }

    companion object {
        private val LOGGER = Global.getLogger(ProjectileGlowPlugin::class.java)

        private val spriteMap: MutableMap<String, String> = HashMap()
        private val xMap: MutableMap<String, Float> = HashMap()
        private val yMap: MutableMap<String, Float> = HashMap()
        private val colorMap: MutableMap<String, Color> = HashMap()

        @JvmStatic
        val glowData: Unit
            get() {
                clearGlowData()

                val glowDataArray = try {
                    LOGGER.info("Reading Glow data")
                    Global.getSettings().getMergedSpreadsheetDataForMod(
                        "id",
                        "data/config/dakka/dakka_GlowData.csv",
                        "zzz_DakkaHolics"
                    )
                } catch (ex: IOException) {
                    logError("Unable to read Glow data", ex)
                    JSONArray()
                } catch (ex: JSONException) {
                    logError("Invalid Glow data format", ex)
                    JSONArray()
                } catch (ex: RuntimeException) {
                    logError("Unexpected error reading Glow data", ex)
                    JSONArray()
                }

                for (index in 0 until glowDataArray.length()) {
                    val row = glowDataArray.optJSONObject(index) ?: continue
                    parseGlowDataRow(row)
                }
                LOGGER.info("Finished reading Glow data")
            }

        /**
         * Clears all existing glow data maps.
         */
        private fun clearGlowData() {
            spriteMap.clear()
            xMap.clear()
            yMap.clear()
            colorMap.clear()
        }

        /**
         * Logs an error with the provided message and exception.
         */
        private fun logError(message: String, exception: Exception) {
            LOGGER.error(message)
            LOGGER.error(exception)
        }

        /**
         * Parses a single row from the Glow data JSON and populates the maps.
         */
        private fun parseGlowDataRow(row: JSONObject) {
            try {
                val glowId = row.getString("id")
                if (spriteMap.containsKey(glowId)) return // Skip duplicates

                spriteMap[glowId] = row.getString("sprite")
                xMap[glowId] = row.getDouble("width").toFloat()
                yMap[glowId] = row.getDouble("length").toFloat()
                colorMap[glowId] = MagicSettings.toColor4(row.getString("color"))
            } catch (ex: JSONException) {
                LOGGER.warn("Skipping invalid row in Glow data: ${row.optString("id", "unknown")}")
            }
        }
    }
}
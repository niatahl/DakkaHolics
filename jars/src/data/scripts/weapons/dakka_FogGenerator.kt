package data.scripts.weapons

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.CollisionUtils
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import java.awt.Color

class dakka_FogGenerator : EveryFrameWeaponEffectPlugin {
    private val interval = IntervalUtil(0.1f, 0.15f)

    override fun advance(amount: Float, engine: CombatEngineAPI, weapon: WeaponAPI) {
        val ship = weapon.ship
        interval.advance(amount)
        if (interval.intervalElapsed()) {
            engine.addNebulaParticle(
                weapon.location,
                MathUtils.getRandomPointInCircle(Misc.ZERO, 50f),
                MathUtils.getRandomNumberInRange(150f, 300f), // size
                0.3f,
                0.5f,
                0.5f,
                MathUtils.getRandomNumberInRange(2.0f, 3.4f), // duration
                Color(42, 19, 56, 80)
            )
            engine.addNegativeNebulaParticle(
                weapon.location,
                MathUtils.getRandomPointInCircle(Misc.ZERO, 50f),
                MathUtils.getRandomNumberInRange(150f, 300f), // size
                0.3f,
                0.5f,
                0.5f,
                MathUtils.getRandomNumberInRange(2.0f, 3.4f), // duration
                Color(24, 255, 228, 40)
            )
        }
    }
}
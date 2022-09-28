package data.scripts

import com.fs.starfarer.api.BaseModPlugin
import data.scripts.plugins.dakka_ProjectileGlowPlugin.Companion.glowData

class dakka_ModPlugin : BaseModPlugin() {
    @Throws(Exception::class)
    override fun onApplicationLoad() {
        glowData
    }
}
package org.niatahl.dakka

import com.fs.starfarer.api.BaseModPlugin
import org.niatahl.dakka.plugins.ProjectileGlowPlugin.Companion.glowData

class DakkaModPlugin : BaseModPlugin() {
    @Throws(Exception::class)
    override fun onApplicationLoad() {
        glowData
    }
}
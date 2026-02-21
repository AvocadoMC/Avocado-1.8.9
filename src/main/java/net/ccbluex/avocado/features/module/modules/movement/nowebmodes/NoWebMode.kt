package net.ccbluex.avocado.features.module.modules.movement.nowebmodes

import net.ccbluex.avocado.utils.client.MinecraftInstance

open class NoWebMode(val modeName: String) : MinecraftInstance {
    open fun onUpdate() {}
}

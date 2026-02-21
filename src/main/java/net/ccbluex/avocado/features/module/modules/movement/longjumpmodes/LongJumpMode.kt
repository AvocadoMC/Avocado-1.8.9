/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.avocado.features.module.modules.movement.longjumpmodes

import net.ccbluex.avocado.event.JumpEvent
import net.ccbluex.avocado.event.MoveEvent
import net.ccbluex.avocado.utils.client.MinecraftInstance

open class LongJumpMode(val modeName: String) : MinecraftInstance {
    open fun onUpdate() {}
    open fun onMove(event: MoveEvent) {}
    open fun onJump(event: JumpEvent) {}

    open fun onEnable() {}
    open fun onDisable() {}
}
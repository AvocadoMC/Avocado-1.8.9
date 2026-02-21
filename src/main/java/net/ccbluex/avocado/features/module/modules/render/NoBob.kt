/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.avocado.features.module.modules.render

import net.ccbluex.avocado.event.MotionEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module

object NoBob : Module("NoBob", Category.RENDER, gameDetecting = false) {

    val onMotion = handler<MotionEvent> {
        mc.thePlayer?.distanceWalkedModified = -1f
    }
}

/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/avocado/
 */
package net.ccbluex.avocado.features.module.modules.movement.flymodes.other

import net.ccbluex.avocado.event.PacketEvent
import net.ccbluex.avocado.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.avocado.utils.extensions.isInLiquid
import net.ccbluex.avocado.utils.extensions.tryJump
import net.ccbluex.avocado.utils.timing.MSTimer
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion

object TNTFlag : FlyMode("TNTFlag") {

    /**
     * @author TheSmartCat
     */

    private val velocityTimer = MSTimer()
    private var hasReceivedVelocity = false

    override fun onDisable() {
        hasReceivedVelocity = false
    }

    override fun onUpdate() {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInLiquid || thePlayer.isInWeb || thePlayer.isDead)
            return

        if (hasReceivedVelocity) {
            if (thePlayer.onGround) {
                thePlayer.tryJump()
            }
            hasReceivedVelocity = false
        }
    }

    override fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return
        val packet = event.packet

        if (event.isCancelled) return

        if ((packet is S12PacketEntityVelocity && thePlayer.entityId == packet.entityID && packet.motionY > 0 && (packet.motionX != 0 || packet.motionZ != 0)) ||
            (packet is S27PacketExplosion && (thePlayer.motionY + packet.field_149153_g) > 0.0 &&
                    ((thePlayer.motionX + packet.field_149152_f) != 0.0 || (thePlayer.motionZ + packet.field_149159_h) != 0.0))) {

            velocityTimer.reset()
            hasReceivedVelocity = true
            event.cancelEvent()
        }
    }
}
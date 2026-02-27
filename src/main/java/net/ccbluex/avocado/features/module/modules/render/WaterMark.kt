/*
 * Avocado Hacked Client
 */
package net.ccbluex.avocado.features.module.modules.render

import net.ccbluex.avocado.Avocado.CLIENT_NAME
import net.ccbluex.avocado.Avocado.moduleManager
import net.ccbluex.avocado.event.Render2DEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.avocado.ui.font.Fonts
import net.ccbluex.avocado.utils.client.ServerUtils
import net.ccbluex.avocado.utils.extensions.getPing
import net.ccbluex.avocado.utils.inventory.SilentHotbar
import net.ccbluex.avocado.utils.render.RenderUtils.drawImage
import net.ccbluex.avocado.utils.render.RenderUtils.drawRoundedBorderRect
import net.ccbluex.avocado.utils.render.RenderUtils.drawRoundedRect
import net.ccbluex.avocado.utils.render.animation.AnimationUtil
import net.ccbluex.avocado.utils.render.ColorUtils.withAlpha
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting
import net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.text.DecimalFormat
import kotlin.math.max

object WaterMark : Module("WaterMark", Category.RENDER) {

    init {
        state = true
    }

    private val ClientName by text("ClientName", "Avocado")
    private val animationSpeed by float("AnimationSpeed", 0.2F, 0.05F..1F)

    private val ColorA_ by int("Red", 255, 0..255)
    private val ColorB_ by int("Green", 255, 0..255)
    private val ColorC_ by int("Blue", 255, 0..255)

    private val BackgroundAlpha by int("BackGroundAlpha", 144, 0..255)

    private val BorderEnabled by boolean("Border", true)
    private val BorderColor by color("BorderColor", Color(255, 0, 0, 255))
    private val BorderWidth by float("BorderWidth", 5f, 1f..5f)

    private val versionNameUp by text("VersionName", "Main")
    private val versionNameDown = CLIENT_NAME

    private val textMode by choices("TextMode", arrayOf("Custom", "Themes"), "Themes")
    private val borderMode by choices("BorderMode", arrayOf("Custom", "Themes"), "Themes")

    private val themesStartColor by color("ThemesStartColor", Color(255, 105, 180))
    private val themesEndColor by color("ThemesEndColor", Color(0, 111, 255))
    private val themesSpeed by float("ThemesSpeed", 1f, 0.1f..5f)

    private var textOffset = 0f
    private var borderOffset = 0f
    private var lastUpdateTime = System.currentTimeMillis()

    private val positionX by float("PositionX", -1f, -1000f..1000f)
    private val positionY by float("PositionY", -1f, -1000f..1000f)

    enum class State {
        Normal, Scaffold
    }

    private var scaledScreen = ScaledResolution(mc)
    private var width = scaledScreen.scaledWidth
    private var height = scaledScreen.scaledHeight
    private var island_State = State.Normal
    private var start_y = (height / 9).toFloat()
    private var AnimStartX = (width / 2).toFloat()
    private var AnimEndX = AnimStartX + 100F

    private fun getGradientColor(progress: Float): Color {
        val r = (themesStartColor.red + (themesEndColor.red - themesStartColor.red) * progress).toInt()
        val g = (themesStartColor.green + (themesEndColor.green - themesStartColor.green) * progress).toInt()
        val b = (themesStartColor.blue + (themesEndColor.blue - themesStartColor.blue) * progress).toInt()
        return Color(r.coerceIn(0,255), g.coerceIn(0,255), b.coerceIn(0,255))
    }

    val onRender2D = handler<Render2DEvent> {

        scaledScreen = ScaledResolution(mc)
        width = scaledScreen.scaledWidth
        height = scaledScreen.scaledHeight

        val now = System.currentTimeMillis()
        val delta = (now - lastUpdateTime) / 1000f
        lastUpdateTime = now
        textOffset = (textOffset + delta * themesSpeed) % 1f
        borderOffset = (borderOffset + delta * themesSpeed) % 1f

        start_y = if (positionY == -1f) (height / 9).toFloat() else positionY
        val baseX = if (positionX == -1f) (width / 2).toFloat() else positionX

        island_State =
            if (moduleManager.getModule("Scaffold")?.state == true)
                State.Scaffold
            else State.Normal

        when (island_State) {
            State.Normal -> drawNormal(baseX)
            State.Scaffold -> drawScaffold(baseX)
        }
    }

    private fun drawContainer(x1: Float, y1: Float, x2: Float, y2: Float) {

        val borderRenderColor = if (borderMode == "Themes") {
            val progress = (x1 / 100 + borderOffset) % 1f
            getGradientColor(progress)
        } else BorderColor
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)

        if (BorderEnabled) {

            drawRoundedRect(
                x1, y1, x2, y2,
                Color(0, 0, 0, BackgroundAlpha).rgb,
                13f
            )
            drawRoundedBorderRect(
                x1, y1, x2, y2,
                BorderWidth,
                0,
                borderRenderColor.rgb,
                13f
            )

        } else {
            drawRoundedRect(
                x1, y1, x2, y2,
                Color(0, 0, 0, BackgroundAlpha).rgb,
                13f
            )
        }

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    private fun drawNormal(baseX: Float) {
        val username = mc.session.username
        val fps = Minecraft.getDebugFPS()
        val ping = mc.thePlayer.getPing()

        val main = " $ClientName"
        val info = " | $username | ${fps}fps | ${ping}ms"

        val iconSize = 21f
        val padding = 2f
        val space = 4f

        val len1 = Fonts.fontSemibold40.getStringWidth(main)
        val len2 = Fonts.fontSemibold40.getStringWidth(info)
        val total = padding + iconSize + space + len1 + len2 + padding

        val startX = baseX - total / 2

        AnimStartX = AnimationUtil.base(AnimStartX.toDouble(), startX.toDouble(), animationSpeed.toDouble()).toFloat()
        AnimEndX = AnimationUtil.base(AnimEndX.toDouble(), (startX + total).toDouble(), animationSpeed.toDouble()).toFloat()

        drawContainer(AnimStartX, start_y, AnimEndX, start_y + 27f)

        drawImage(
            ResourceLocation("${CLIENT_NAME.lowercase()}/icon_64x64.png"),
            (startX + padding + 2).toInt(),
            (start_y + 4).toInt(),
            19, 19,
            Color(ColorA_, ColorB_, ColorC_)
        )

        val textX = startX + padding + iconSize + space
        val textY = start_y + 9f

        if (textMode == "Themes") {
            drawGradientText(main + info, textX, textY)
        } else {
            drawStringShadow(Fonts.fontSemibold40, main, textX, textY, Color(ColorA_,ColorB_,ColorC_).rgb)
            drawStringShadow(Fonts.fontSemibold40, info, textX + len1, textY, Color.WHITE.rgb)
        }
    }

    private fun drawStringShadow(
        font: net.ccbluex.avocado.ui.font.GameFontRenderer,
        text: String,
        x: Float,
        y: Float,
        color: Int
    ) {
        font.drawString(text, x + 1f, y + 1f, 0x78000000)

        font.drawString(text, x, y, color)
    }

    private val DECIMAL_FORMAT = DecimalFormat("0.00")
    private val progressLen = 120f
    private var progressAnim = progressLen

    private fun drawScaffold(baseX: Float) {
        val stack = mc.thePlayer.inventory.getStackInSlot(SilentHotbar.currentSlot)
        val blockCount = stack?.stackSize ?: 0
        val pitch = DECIMAL_FORMAT.format(mc.thePlayer.rotationPitch)

        val icon = 23f
        val pad = 2f
        val barHeight = 3f

        val textWidth = Fonts.fontSemibold40.getStringWidth("$blockCount blocks")

        val total = pad + icon + pad + progressLen + pad + 4f + textWidth + pad
        val startX = baseX - total / 2

        AnimStartX = AnimationUtil.base(AnimStartX.toDouble(), startX.toDouble(), animationSpeed.toDouble()).toFloat()
        AnimEndX = AnimationUtil.base(AnimEndX.toDouble(), (startX + total).toDouble(), animationSpeed.toDouble()).toFloat()

        val realBar = pad + icon + pad + (progressLen / 64f * blockCount)
        progressAnim = AnimationUtil.base(progressAnim.toDouble(), realBar.toDouble(), animationSpeed.toDouble()).toFloat()

        drawContainer(AnimStartX, start_y, AnimEndX, start_y + 27f)

        drawRoundedRect(startX + pad + icon + pad,
            start_y + 27f/2 - barHeight/2,
            startX + pad + icon + pad + progressLen,
            start_y + 27f/2 + barHeight/2,
            Color(0,0,0,200).rgb, 3f)

        drawRoundedRect(startX + pad + icon + pad,
            start_y + 27f/2 - barHeight/2,
            startX + progressAnim,
            start_y + 27f/2 + barHeight/2,
            Color.WHITE.rgb, 3f)

        val textX = startX + pad + icon + pad + progressLen + pad + 3f

        drawStringShadow(Fonts.fontSemibold40, "$blockCount blocks", textX, start_y + 4.5f, Color.WHITE.rgb)
        drawStringShadow(Fonts.fontSemibold35, "$pitch ", textX, start_y + 14f, Color(140,140,140).rgb)

        GlStateManager.pushMatrix()
        enableGUIStandardItemLighting()
        if (stack?.item is ItemBlock)
            mc.renderItem.renderItemAndEffectIntoGUI(stack, (startX+pad+4).toInt(), (start_y+4).toInt())
        disableStandardItemLighting()
        GlStateManager.popMatrix()
    }

    private fun drawGradientText(text: String, x: Float, y: Float) {
        var offset = 0f
        for (char in text) {
            val progress = ((x + offset) / 200 + textOffset) % 1f
            val color = getGradientColor(progress)
            drawStringShadow(Fonts.fontSemibold40, char.toString(), x + offset, y, color.rgb)
            offset += Fonts.fontSemibold40.getStringWidth(char.toString())
        }
    }
}
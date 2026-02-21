/*
 * Avocado Hacked Client
 */
package net.ccbluex.avocado.injection.forge.mixins.gui;

import net.ccbluex.avocado.ui.font.AWTFontRenderer;
import net.ccbluex.avocado.ui.font.Fonts;
import net.ccbluex.avocado.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.*;

import java.awt.*;

import static net.minecraft.client.renderer.GlStateManager.resetColor;

@Mixin(GuiButton.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiButton extends Gui {

    @Shadow public boolean visible;
    @Shadow public int xPosition;
    @Shadow public int yPosition;
    @Shadow public int width;
    @Shadow public int height;
    @Shadow protected boolean hovered;
    @Shadow public boolean enabled;
    @Shadow public String displayString;
    @Shadow @Final protected static ResourceLocation buttonTextures;

    @Shadow
    protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

    @Overwrite
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;

        hovered = mouseX >= xPosition && mouseY >= yPosition &&
                mouseX < xPosition + width && mouseY < yPosition + height;

        float radius = 3.0F;

        int backgroundColor = enabled
                ? new Color(0, 0, 0, 120).getRGB()
                : new Color(120, 120, 120, 120).getRGB();

        RenderUtils.INSTANCE.drawRoundedRect(
                xPosition,
                yPosition,
                xPosition + width,
                yPosition + height,
                backgroundColor,
                radius,
                RenderUtils.RoundedCorners.ALL
        );

        if (enabled && hovered) {
            RenderUtils.INSTANCE.drawRoundedRect(
                    xPosition,
                    yPosition,
                    xPosition + width,
                    yPosition + height,
                    new Color(255, 255, 255, 30).getRGB(),
                    radius,
                    RenderUtils.RoundedCorners.ALL
            );
        }

        mouseDragged(mc, mouseX, mouseY);

        AWTFontRenderer.Companion.setAssumeNonVolatile(true);

        FontRenderer font = mc.fontRendererObj;

        int textColor = enabled ? 0xE0E0E0 : 0xA0A0A0;

        font.drawStringWithShadow(
                displayString,
                xPosition + width / 2F - font.getStringWidth(displayString) / 2F,
                yPosition + (height - 5) / 2F,
                textColor
        );

        AWTFontRenderer.Companion.setAssumeNonVolatile(false);
        resetColor();
    }
}
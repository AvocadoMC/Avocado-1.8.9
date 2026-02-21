package net.ccbluex.avocado.injection.forge.mixins.gui;

import net.minecraft.client.gui.GuiSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiSlot.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiSlot {
}
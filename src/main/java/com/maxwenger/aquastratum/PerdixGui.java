package com.maxwenger.aquastratum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class PerdixGui extends GuiScreen {

    ResourceLocation texture = new ResourceLocation(AquaStratum.MODID, "textures/gui/perdix.png");
    int guiWidth = 246;
    int guiHeight = 214;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        drawTexturedModalRect(0, 0, 0, 0, guiWidth, guiHeight);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

}

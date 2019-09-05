package com.maxwenger.aquastratum;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class EventSubscriptions {

    private DiverProfile diverProfile;
    private Minecraft mc;
    private Perdix perdix;

    public void setDiverProfile(DiverProfile diverProfile) {
        this.diverProfile = diverProfile;
    }

    public void setMC(Minecraft mc) {
        this.mc = mc;
    }

    public void setPerdix(Perdix perdix) {
        this.perdix = perdix;
    }

    // 20 ticks per second, want it to run at 2hz
    private int tickCounter = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        int epochsPerSecond = 2;
        if(tickCounter >= 20/epochsPerSecond) {
            double secondsPerEpoch = 1 / epochsPerSecond;

            if (diverProfile != null) {
                diverProfile.RecomputeTissues(secondsPerEpoch);
            }

            tickCounter = 0;
        } else {
            tickCounter++;
        }
    }

    @SubscribeEvent
    public void onDebugOverlay(RenderGameOverlayEvent.Text event){
        if (mc != null && diverProfile != null){
            if (mc.gameSettings.showDebugInfo && diverProfile.isDiving()) {
                int depth = diverProfile.getDepth();
                double Pa = 1 + (depth / 10.0);
                event.getLeft().add("Pa: " + Pa + " bar; " + "Depth: " + depth + "m");
            }
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if(Keyboard.isKeyDown(Keyboard.KEY_C)){
            mc.displayGuiScreen(perdix);
        }
    }
}

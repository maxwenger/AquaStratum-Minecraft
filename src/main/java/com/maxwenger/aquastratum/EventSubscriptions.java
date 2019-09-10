package com.maxwenger.aquastratum;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;

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
        double epochsPerSecond = 2.0;
        if (tickCounter >= 20.0 / epochsPerSecond) {
            double secondsPerEpoch = 1.0 / epochsPerSecond;

            if (diverProfile != null && mc.player != null) {
                diverProfile.RecomputeTissues(secondsPerEpoch);
            }

            tickCounter = 0;
        } else {
            tickCounter++;
        }
    }

    @SubscribeEvent
    public void onDebugOverlay(RenderGameOverlayEvent.Text event) {
        if (mc != null && diverProfile != null) {
            if (mc.gameSettings.showDebugInfo && diverProfile.isDiving()) {
                int depth = diverProfile.getDepth();
                double Pa = 1 + (depth / 10.0);
                DecimalFormat floatF = new DecimalFormat("0.000");
                DecimalFormat intF = new DecimalFormat("00");
                event.getRight().add("Pa: " + Pa + " bar; " + "Depth: " + depth + "m");
                event.getRight().add("Curve Deco Stress: " + floatF.format(diverProfile.getCurveStress()));
                for(int i=1; i < 16; i += 2){
                    int j = i-1;
                    int k = i;
                    event.getLeft().add("Tissue " + intF.format(j) + ": [ZHL: " + floatF.format(diverProfile.getZHLTP(j)) + " Curve: " + floatF.format(diverProfile.getCurveTP(j))+"]"
                            + " | Tissue " + intF.format(k) + ": [ZHL: " + floatF.format(diverProfile.getZHLTP(k)) + " Curve: " + floatF.format(diverProfile.getCurveTP(k))+"]");
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            mc.displayGuiScreen(perdix);
        }
    }
}

package com.maxwenger.aquastratum;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;

public class EventSubscriptions {

    private DiverProfile diverProfile;
    private Minecraft mc;
    private PerdixGui perdixGui;

    public void setDiverProfile(DiverProfile diverProfile) {
        this.diverProfile = diverProfile;
    }

    public void setMC(Minecraft mc) {
        this.mc = mc;
    }

    public void setPerdixGui(PerdixGui perdixGui) {
        this.perdixGui = perdixGui;
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
                double depth = diverProfile.getDepth();
                double Pa = 1 + (depth / 10.0);
                DecimalFormat floatF = new DecimalFormat("0.000");
                DecimalFormat intF = new DecimalFormat("00");
                event.getRight().add("Pa: " + floatF.format(Pa) + " bar; " + "Depth: " + floatF.format(depth) + "m");
                event.getRight().add("Curve Deco Stress: " + floatF.format(diverProfile.getCurveStress()) + "; Ceiling: " + floatF.format(diverProfile.getCurveCeiling()) + "m");
                event.getLeft().add("Ceiling: " + floatF.format(diverProfile.getZHLCeiling()) + "m; P: " + floatF.format(diverProfile.getZhl16A().getPressureCeling()));
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
    public void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().registerAll();
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            mc.displayGuiScreen(perdixGui);
        }
    }
}

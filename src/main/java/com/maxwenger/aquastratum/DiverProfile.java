package com.maxwenger.aquastratum;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;

public class DiverProfile {

    private Minecraft mc;
    private ZHL16A zhl16A;
    private ContinuousCurve continuousCurve;
    private GasMix gasMix;

    private double gfLow = 0.2;
    private double gfHigh = 0.85;

    public DiverProfile(Minecraft mc) {
        this.mc = mc;
        gasMix = new GasMix("21/00");
        continuousCurve = new ContinuousCurve(gasMix);
        zhl16A = new ZHL16A(gasMix.getO2Percent(), gfLow, gfHigh);
    }

    public boolean isDiving() {
        return getPlayer().isInWater() && getDepth() > 0;
    }

    public void RecomputeTissues(double deltaTime) {
        zhl16A.RecomputeN2Compartments(getPPOG(gasMix.getN2Percent()), deltaTime);
        continuousCurve.update(getDepth(), deltaTime);
    }

    public double getPPO2() {
        return getPPOG(gasMix.getO2Percent());
    }

    public double getDepth() {
        double playerY = getPlayer().posY;
        int cursorPos = (int)Math.floor(playerY);
        int x = (int)Math.floor(getPlayer().posX);
        int z = (int)Math.floor(getPlayer().posZ);
        double depth = 0; //Start with a fractional offset
        while(mc.world.getBlockState(new BlockPos(x, cursorPos, z)).getMaterial().isLiquid() && cursorPos < 255){
            depth += 1;
            cursorPos += 1;
        }
        depth += playerY % 1;
        return depth;
    }

    private double getPPOG(double fog) {
        double depth = getDepth();
        double ppog = fog;

        if (depth != 0) {
            ppog *= 1 + (depth / 10);
        }

        return ppog;
    }

    public double getZHLTP(int tissueIndex){
        return zhl16A.GetCompartments()[tissueIndex].GetCurrentPPOG();
    }

    public int getCeiling() {
        double ceiling = zhl16A.getPressureCeling() * 10;

        if (ceiling < 0) {
            ceiling = 0;
        }

        return (int)Math.ceil(ceiling);
    }

    public double getCurveTP(double tissueIndex){
        return continuousCurve.getTissue(tissueIndex);
    }

    public double getCurveStress(){
        return continuousCurve.decoStress();
    }

    private EntityPlayerSP getPlayer() {
        return mc.player;
    }
}

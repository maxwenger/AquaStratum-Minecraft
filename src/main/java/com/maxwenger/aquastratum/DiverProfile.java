package com.maxwenger.aquastratum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

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
        continuousCurve = new ContinuousCurve();
        zhl16A = new ZHL16A(gasMix.getO2Percent(), gfLow, gfHigh);
    }

    public boolean isDiving() {
        return getPlayer().isInWater() && getDepth() > 0;
    }

    public void RecomputeTissues(double deltaTime) {
        zhl16A.RecomputeN2Compartments(getPPOG(gasMix.getN2Percent()), deltaTime);
        continuousCurve.addElement(getDepth(), deltaTime, gasMix);
    }

    public double getPPO2() {
        return getPPOG(gasMix.getO2Percent());
    }

    public ZHL16A getZhl16A() {
        return zhl16A;
    }

    public int getCeiling() {
        double ceiling = zhl16A.getPressureCeling() * 10;

        if (ceiling < 0) {
            ceiling = 0;
        }

        return (int)Math.ceil(ceiling);
    }

    public int getAltitude() {
        int seaLevel = 63;
        int yCord = getPlayer().getPosition().getY();
        return yCord - seaLevel;
    }

    public int getDepth() {

        int alt = getAltitude();
        if (alt < 0 && getPlayer().isInWater()) {
            return getAltitude() * -1;
        }

        return 0;
    }

    private double getPPOG(double fog) {
        int depth = getDepth();
        double ppog = fog;

        if (depth != 0) {
            ppog *= 1 + (depth / 10);
        }

        return ppog;
    }

    public double getZHLTP(int tissueIndex){
        return zhl16A.GetCompartments()[tissueIndex].GetCurrentPPOG();
    }

    public double getCurveTP(double tissueIndex){
        return continuousCurve.getCurve(tissueIndex);
    }

    public double getCurveStress(){
        return continuousCurve.decoStress();
    }

    private EntityPlayerSP getPlayer() {
        return mc.player;
    }
}

package com.maxwenger.aquastratum;

public class ZHLTissueCompartment {

    private double HalfTime;
    private double CurrentPPOG;

    private double a;
    private double b;

    public ZHLTissueCompartment(double halfTime, double initialPPOG) {
        this.HalfTime = halfTime;
        CurrentPPOG = initialPPOG;

        a = 2.0 * Math.pow(halfTime, -1.0 / 3);
        b = 1.005 - Math.pow(halfTime, -0.5);
    }

    // ppog: partial pressure of gas, in bar.
    // time: measured in seconds.
    public double ComputePPOG(double PPOG, double time) {
        time = time / 60;

        // halftimes are in mins, so we compute based off mins.
        CurrentPPOG += (PPOG - CurrentPPOG) * (1 - Math.pow(2, ((-1 * time) / HalfTime)));
        return CurrentPPOG;
    }

    public double getPressureCeiling() {
        return (CurrentPPOG - a) * b;
    }

    public double GetCurrentPPOG() {
        return CurrentPPOG;
    }
}

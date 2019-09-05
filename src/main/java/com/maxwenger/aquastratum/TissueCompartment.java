package com.maxwenger.aquastratum;

public class TissueCompartment {

    private double HalfTime;
    private double CurrentPPOG;

    public TissueCompartment(double halfTime, double initialPPOG) {
        this.HalfTime = halfTime;
        CurrentPPOG = initialPPOG;
    }

    // ppog: partial pressure of gas, in bar.
    // time: measured in seconds.
    public double ComputePPOG(double PPOG, double time) {
        time = time/60;

        // halftimes are in mins, so we compute based off mins.
        CurrentPPOG += (PPOG - CurrentPPOG) * (1 - Math.pow(2, ((-1*time)/HalfTime)));
        return CurrentPPOG;
    }

    public double GetCurrentPPOG() {
        return CurrentPPOG;
    }
}

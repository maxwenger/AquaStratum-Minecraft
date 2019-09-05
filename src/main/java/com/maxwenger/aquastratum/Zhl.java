package com.maxwenger.aquastratum;

public class Zhl {
    double[] n2HalfTimes = new double[]{4, 8, 12.5, 18.5, 27, 38.3, 54.3, 77, 109, 146, 187, 239, 305, 390, 498, 635};
    TissueCompartment[] n2Compartments = new TissueCompartment[16];
    double fo2;
    double fn2;

    public Zhl(double fo2) {
        InitTissueCompartments();
        this.fo2 = fo2;
        this.fn2 = 1 - fo2;
    }

    public TissueCompartment[] GetCompartments() {
        return n2Compartments;
    }

    public int GetControllingCompartmentIndex() {
        int controllingCompartment = 0;
        for (int i = 0; i < n2Compartments.length; i++){
            if (n2Compartments[i].GetCurrentPPOG() > n2Compartments[controllingCompartment].GetCurrentPPOG()){
                controllingCompartment = i;
            }
        }
        return controllingCompartment;
    }

    // ppn2: partial pressure of nitrogen measured in bar
    // deltaTime: time sense last computation, measured in seconds.
    public void RecomputeN2Compartments(double ppn2, double deltaTime){

        for (int i = 0; i < n2Compartments.length; i++) {
            n2Compartments[i].ComputePPOG(ppn2, deltaTime);
        }
    }

    private void InitTissueCompartments() {
        for (int i = 0; i < n2Compartments.length; i++){
            n2Compartments[i] = new TissueCompartment(n2HalfTimes[i], fn2);
        }
    }
}

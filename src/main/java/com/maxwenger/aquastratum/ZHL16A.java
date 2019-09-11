package com.maxwenger.aquastratum;

public class ZHL16A {
    double[] n2HalfTimes = new double[]{4, 8, 12.5, 18.5, 27, 38.3, 54.3, 77, 109, 146, 187, 239, 305, 390, 498, 635};
    ZHLTissueCompartment[] n2Compartments = new ZHLTissueCompartment[16];
    double fo2;
    double fn2;

    double gfLow;
    double gfHigh;

    public ZHL16A(double fo2, double gfLow, double gfHigh) {
        InitTissueCompartments();
        this.fo2 = fo2;
        this.fn2 = 1 - fo2;

        this.gfLow = gfLow;
        this.gfHigh = gfHigh;
    }

    public ZHLTissueCompartment[] GetCompartments() {
        return n2Compartments;
    }

    public double getCeling() {
        double maxPPoG = getControllingCompartment().getMaxToleratedPressure();
        double maxAmbientPressure = maxPPoG / fn2;

        return maxAmbientPressure;
    }

    public ZHLTissueCompartment getControllingCompartment() {
        int controllingCompartment = 0;
        for (int i = 0; i < n2Compartments.length; i++){
            if (n2Compartments[i].GetCurrentPPOG() > n2Compartments[controllingCompartment].GetCurrentPPOG()){
                controllingCompartment = i;
            }
        }
        return n2Compartments[controllingCompartment];
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
            n2Compartments[i] = new ZHLTissueCompartment(n2HalfTimes[i], fn2);
        }
    }
}

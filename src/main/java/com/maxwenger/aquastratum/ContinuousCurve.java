package com.maxwenger.aquastratum;

import org.apache.commons.math3.analysis.integration.*;

//Right now this code is assuming only N2, but everything else is able to handle other mixes
public class ContinuousCurve extends DecoAlgorithm {
    private ProfileElement[] profile;

    private double testPressure; //Used for calculating the ceiling, really should not be a field but I can't come up with any better solution

    private static final double TOLERANCE = 0.01;
    private static final int TISSUE_MAX = 15;
    private static final int TISSUE_MIN = 0;
    private static final double MAX_STRESS = 5; // This controls the conservativeness of the algo, lower is more conservative, 5 happens to be roughly zhl, by observation
    private static final double CEILING_STEP = 0.01; // This controls the amount of sigfigs in the ceiling, warning, major performance penalty

    public ContinuousCurve(){
        super();
        profile = new ProfileElement[0];
    }

    public ContinuousCurve(GasMix startingMix){
        super(startingMix);
        profile = new ProfileElement[0];
    }

    public ContinuousCurve(ContinuousCurve continuousCurve){
        super(continuousCurve);
        profile = continuousCurve.profile.clone();
    }

    private double halftimeN2(double x) {
        return 5.065 * Math.exp(0.3176 * (x));
    }

    private double timeConstant(double x) {
        return Math.log(2) / halftimeN2(x);
    }

    private double alphaFunction(int index, double x) {
        return profile[index].getPN2() - profile[index].getdPN2() / timeConstant(x);
    }

    private double betaFunction(int index, double x) {
        return profile[index].getPN2() + profile[index].getdPN2() * (profile[index].getTime() - 1 / timeConstant(x));
    }

    private double getCurveRec(int index, double x){
        if(index == -1){
            return profile[0].getGasMix().getN2Percent();
        }
        ProfileElement element = profile[index];
        double priorCurve = getCurveRec(index-1, x);
        double exponent = Math.exp(-timeConstant(x)*element.getTime());
        return betaFunction(index, x)-(alphaFunction(index, x)-priorCurve)*exponent;
    }

    public double decoStress(){
        TrapezoidIntegrator integrator = new TrapezoidIntegrator(TOLERANCE,TOLERANCE,TrapezoidIntegrator.DEFAULT_MIN_ITERATIONS_COUNT, TrapezoidIntegrator.TRAPEZOID_MAX_ITERATIONS_COUNT);
        return integrator.integrate(Integer.MAX_VALUE, this::overPressure, TISSUE_MIN, TISSUE_MAX);
    }

    public double decoStress(double ambientPressure){
        testPressure = ambientPressure;
        TrapezoidIntegrator integrator = new TrapezoidIntegrator(TOLERANCE,TOLERANCE,TrapezoidIntegrator.DEFAULT_MIN_ITERATIONS_COUNT, TrapezoidIntegrator.TRAPEZOID_MAX_ITERATIONS_COUNT);
        return integrator.integrate(Integer.MAX_VALUE, this::overPressureA, TISSUE_MIN, TISSUE_MAX);
    }

    private double overPressure(double x){
        double ambientPressure = profile[profile.length-1].getAmbientPressure();
        double tissueOverPressure = getTissue(x) - stressLine(x,ambientPressure);
        if(tissueOverPressure <= 0){
            tissueOverPressure = 0;
        }
        return tissueOverPressure;
    }

    private double overPressureA(double x){
        double ambientPressure = testPressure;
        double tissueOverPressure = getTissue(x) - stressLine(x,ambientPressure);
        if(tissueOverPressure <= 0){
            tissueOverPressure = 0;
        }
        return tissueOverPressure;
    }

    private double stressLine(double x, double ambientPressure){
        return ambientPressure;
    }

    @Override
    public double getTissue(double index) {
        return getCurveRec(profile.length-1, index);
    }

    @Override
    public void update(double depth, double time) {
        ProfileElement newElement;
        if (profile.length == 0) {
            newElement = new ProfileElement(depth, time, super.currentMix);
        } else {
            ProfileElement priorElement = profile[profile.length - 1];
            newElement = new ProfileElement(depth, time, super.currentMix, priorElement);
        }
        ProfileElement[] newProfile = new ProfileElement[profile.length + 1];
        for (int i = 0; i < profile.length; i++) {
            newProfile[i] = profile[i];
        }
        newProfile[profile.length] = newElement;
        profile = newProfile;
    }

    @Override
    public double getCeiling() {
        double testAmbient = 1;
        while(decoStress(testAmbient) > MAX_STRESS){
            testAmbient += CEILING_STEP;
        }
        return testAmbient;
    }
}

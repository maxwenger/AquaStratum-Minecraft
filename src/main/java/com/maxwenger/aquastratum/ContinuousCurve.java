package com.maxwenger.aquastratum;

import org.apache.commons.math3.analysis.integration.*;

//Right now this code is assuming only N2, but everything else is able to handle other mixes
public class ContinuousCurve extends DecoAlgorithm {
    private ProfileElement[] profile;

    private static final double TOLERANCE = 0.01;
    private static final int TISSUEMAX = 15;
    private static final int TISSUEMIN = 0;

    public ContinuousCurve(){
        super();
        profile = new ProfileElement[0];
    }

    public ContinuousCurve(GasMix startingMix){
        super(startingMix);
        profile = new ProfileElement[0];
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
        return integrator.integrate(Integer.MAX_VALUE, this::overPressure,TISSUEMIN,TISSUEMAX);
    }

    private double overPressure(double x){
        double tissueOverPressure = getTissue(x) - stressLine(x);
        if(tissueOverPressure <= 0){
            tissueOverPressure = 0;
        }
        return tissueOverPressure;
    }

    private double stressLine(double x){
        return profile[profile.length-1].getAmbientPressure();
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
        return 0;
    }
}

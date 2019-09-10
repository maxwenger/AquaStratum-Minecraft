package com.maxwenger.aquastratum;

import org.apache.commons.math3.analysis.integration.*;

//Right now this code is assuming only N2, but everything else is able to handle other mixes
public class ContinuousCurve {
    private ProfileElement[] profile;

    private static final double TOLERANCE = 0.01;
    private static final double STEP = 0.05;
    private static final int TISSUEMAX = 15;

    public ContinuousCurve(){
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

    public double getCurve(double x) {
        return getCurveRec(profile.length-1, x);
    }

    public double getCurveRec(int index, double x){
        if(index == -1){
            return profile[0].getGasMix().getN2Percent();
        }
        ProfileElement element = profile[index];
        double priorCurve = getCurveRec(index-1, x);
        double exponent = Math.exp(-timeConstant(x)*element.getTime());
        return betaFunction(index, x)-(alphaFunction(index, x)-priorCurve)*exponent;
    }

    public void addElement(double depth, double time, GasMix mix) {
        ProfileElement newElement;
        if (profile.length == 0) {
            newElement = new ProfileElement(depth, time, mix);
        } else {
            ProfileElement priorElement = profile[profile.length - 1];
            newElement = new ProfileElement(depth, time, mix, priorElement);
        }
        ProfileElement[] newProfile = new ProfileElement[profile.length + 1];
        for (int i = 0; i < profile.length; i++) {
            newProfile[i] = profile[i];
        }
        newProfile[profile.length] = newElement;
        profile = newProfile;
    }

    public double decoStress(){
        TrapezoidIntegrator integrator = new TrapezoidIntegrator(TOLERANCE,TOLERANCE,TrapezoidIntegrator.DEFAULT_MIN_ITERATIONS_COUNT, TrapezoidIntegrator.TRAPEZOID_MAX_ITERATIONS_COUNT);
        double upperLimit = getUpperIntersect();
        double lowerLimit = getLowerIntersect(upperLimit);
        return integrator.integrate(Integer.MAX_VALUE, this::stressIntegrand,lowerLimit,upperLimit);
    }

    private double stressIntegrand(double x){
        return getCurve(x)-stressLine(x);
    }

    private double stressLine(double x){
        return profile[profile.length-1].getAmbientPressure();
    }

    private double getUpperIntersect(){
        for(double i = TISSUEMAX; i > 0; i -= STEP){
            if(Math.abs(getCurve(i)-stressLine(i)) < TOLERANCE){
                return i;
            }
        }
        return TISSUEMAX;
    }

    private double getLowerIntersect(double upperLimit){
        if(getCurve(0) > stressLine(0))
        {
            return 0;
        }
        else {
            for (double i = 0; i < upperLimit - 2 * STEP; i += STEP) {
                if (Math.abs(getCurve(i) - stressLine(i)) < TOLERANCE) {
                    return i;
                }
            }
        }
        return 0;
    }
}

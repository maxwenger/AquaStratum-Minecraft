package com.maxwenger.aquastratum;

//Right now this code is assuming only N2, but everything else is able to handle other mixes
public class ContinuousCurve {
    private ProfileElement[] profile;

    public ContinuousCurve(){
        profile = new ProfileElement[0];
    }

    private double halftimeN2(double x) {
        return 5.065 * Math.exp(0.3176 * x);
    }

    private double timeConstant(double x) {
        return Math.log(2) / halftimeN2(x);
    }

    private double alphaFunction(int index, double x) {
        if (index > profile.length-1) {
            return 0;
        }
        return profile[index].getPN2() - profile[index].getdPN2() / timeConstant(x);
    }

    private double betaFunction(int index, double x) {
        if(index == 0){
            return profile[0].getGasMix().getN2Percent();
        }
        return profile[index].getPN2() + profile[index].getdPN2() * (profile[index].getTime() - 1 / timeConstant(x));
    }

    public double getCurve(double x) {
        double totalSum = 0;
        for (int i = 0; i < profile.length; i++) {
            double timeSum = 0;
            for (int j = i+1; j < profile.length; j++) {
                timeSum += profile[j].getTime();
            }
            double exponent = (i-profile.length) * timeConstant(x) * timeSum;
            double difference = (betaFunction(i, x) - alphaFunction(i + 1, x));
            totalSum += difference * Math.exp(exponent);
        }
        return totalSum;
    }

    public void addElement(double depth, double time, GasMix mix) {
        ProfileElement newElement;
        if (profile.length == 0) {
            newElement = new ProfileElement(depth, time, mix);
        } else {
            ProfileElement priorElement = profile[profile.length - 1];
            newElement = new ProfileElement(depth, time, mix/*, priorElement*/);
        }
        ProfileElement[] newProfile = new ProfileElement[profile.length + 1];
        for (int i = 0; i < profile.length; i++) {
            newProfile[i] = profile[i];
        }
        newProfile[profile.length] = newElement;
        profile = newProfile;
    }

}

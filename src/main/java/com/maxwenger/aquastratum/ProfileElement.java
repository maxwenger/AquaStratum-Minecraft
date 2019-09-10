package com.maxwenger.aquastratum;

public class ProfileElement {
    private double ambientPressure; //replaces depth
    private double descentRate;
    private double time;
    private GasMix gasMix;
    private double PN2;
    private double PHe;

    public ProfileElement(double depth, double time, GasMix mix){
        setDepth(depth);
        setTime(time);
        gasMix = mix;
        setPartialPressures(mix);
        setDescentRate(0);
    }

    public ProfileElement(double depth, double time, GasMix mix, ProfileElement priorElement){
        this(depth, time, mix);
        setDescentRate((ambientPressure-priorElement.getAmbientPressure())/time);
    }

    public void setDepth(double depth) {
        double ambientPressure = depthToBar(depth);
        if(ambientPressure < 1){
            throw new IllegalArgumentException("Depth cannot be negative");
        }
        this.ambientPressure = ambientPressure;
    }

    public void setTime(double time) {
        if(time < 0){
            throw new IllegalArgumentException("Time cannot be negative");
        }
        this.time = time/60; //Save as minutes
    }

    public GasMix getGasMix(){
        return gasMix;
    }

    public void setDescentRate(double descentRate){
        this.descentRate = descentRate;
    }

    public void setPartialPressures(GasMix mix){
        PN2 = mix.getN2Percent() * ambientPressure;
        PHe = mix.getN2Percent() * ambientPressure;
    }

    private double depthToBar(double depth){
        return depth/10 + 1;
    }

    private double barToDepth(double ambientPressure){
        return (ambientPressure-1)*10;
    }

    public double getDepth(){
        return barToDepth(ambientPressure);
    }

    public double getAmbientPressure(){
        return ambientPressure;
    }

    public double getTime(){
        return time;
    }

    public double getPN2(){
        return PN2;
    }

    public double getPHe(){
        return PHe;
    }

    public double getdPN2(){
        return PN2*descentRate;
    }

    public double getdPHe(){
        return PHe*descentRate;
    }


}

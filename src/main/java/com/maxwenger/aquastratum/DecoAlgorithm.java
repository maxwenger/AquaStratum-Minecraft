package com.maxwenger.aquastratum;

public abstract class DecoAlgorithm {
    protected GasMix currentMix;

    public abstract double getTissue(double index);
    public abstract void update(double depth, double time);
    public abstract double getCeiling();

    public DecoAlgorithm(){
        setCurrentMix(new GasMix("21/00"));
    }

    public DecoAlgorithm(GasMix startingMix){
        setCurrentMix(startingMix);
    }

    protected void setCurrentMix(GasMix gasMix){
        currentMix = new GasMix(gasMix);
    }

    protected GasMix readCurrentMix(GasMix gasMix){
        return currentMix;
    }
}

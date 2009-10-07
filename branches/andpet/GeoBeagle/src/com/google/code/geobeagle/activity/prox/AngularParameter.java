package com.google.code.geobeagle.activity.prox;

/** Represents an angle in degrees, knowing values 360 and 0 are the same */
class AngularParameter extends Parameter {

    public AngularParameter(double accelConst, double attenuation, double init) {
        super(accelConst, attenuation, init);
    }

    public AngularParameter(double accelConst, double attenuation) {
        super(accelConst, attenuation);
    }
    
    public AngularParameter(double init) {
        super(init);
    }
    
    public AngularParameter() {
        super();
    }
   
    @Override
    public void update(double deltaSec) {
        double distance = mTargetValue - mValue;
        if (distance > 180)
            distance -= 360;
        else if (distance < -180)
            distance += 360;
        
        double accel = distance * Math.abs(distance) * mAccelConst;
        mValue += mChangePerSec * deltaSec + accel*deltaSec*deltaSec/2.0;
        mChangePerSec += accel * deltaSec;
        mChangePerSec *= Math.pow(mAttenuation, deltaSec);
    }
}
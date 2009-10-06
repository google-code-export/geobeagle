package com.google.code.geobeagle.activity.prox;

/** Represents an angle in degrees, knowing values 360 and 0 are the same */
class AngularParameter implements Parameter {

    /** The value being moved towards */
    private double mTargetValue;
    
    /** Current value */
    private double mValue;

    /** Delta per second for mValue. Updated to move mValue towards mTargetValue */
    private double mChangePerSec;

    private final double mAccelConst;
    /** Part of the speed that is preserved after one second if otherwise unaffected. */
    private final double mAttenuation;
    
    /** Good values: 0.8, 0.3*/
    public AngularParameter(double accelConst, double attenuation) {
        mAccelConst = accelConst;
        mAttenuation = attenuation;
    }
    
    @Override
    public double get() {
        return mValue;
    }

    @Override
    public void set(double value) {
        mTargetValue = value % 360.0;
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
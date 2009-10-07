/**
 * 
 */
package com.google.code.geobeagle.activity.prox;

class ScalarParameter extends Parameter {

    public ScalarParameter(double accelConst, double attenuation, double init) {
        super(accelConst, attenuation, init);
    }

    public ScalarParameter(double accelConst, double attenuation) {
        super(accelConst, attenuation);
    }
    
    public ScalarParameter(double init) {
        super(init);
    }
    
    public ScalarParameter() {
        super();
    }

    /** Animate the value towards its goal given that deltaSec time 
     * elapsed since last update */
    public void update(double deltaSec) {
        if (!mIsInited || (mTargetValue == mValue && mChangePerSec == 0))
            return;
        //First update mChangePerSec, then update mValue
        //There is an ideal mChangePerSec for every distance. Move towards it.
        double distance = mTargetValue - mValue;
        double accel = distance * mAccelConst;
        mValue += mChangePerSec * deltaSec + accel*deltaSec*deltaSec/2.0;
        mChangePerSec += accel * deltaSec;
        mChangePerSec *= Math.pow(mAttenuation, deltaSec);
    }
}
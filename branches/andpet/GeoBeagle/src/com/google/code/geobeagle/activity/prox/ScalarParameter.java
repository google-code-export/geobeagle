/**
 * 
 */
package com.google.code.geobeagle.activity.prox;

class ScalarParameter implements Parameter {
    /** Max change in movement towards target value, in delta / (sec^2) */
    private double mValue;
    private double mTargetValue;
    /** Delta per second for mValue. Updated to move mValue towards mTargetValue */
    private double mChangePerSec = 0;
    public ScalarParameter(double init) {
        mTargetValue = init;
        mValue = init;
    }
    public double get() { return mValue; }
    public void set(double value) { mTargetValue = value; }
    /** Animate the value towards its goal given that deltaSec time 
     * elapsed since last update */

    double mAccelConst = 0.8;  //Should be parameter
    //double mAccel;
    /** Part of the speed that is preserved after one second if otherwise unaffected. */
    private final double mAttenuation = 0.3;
    public void update(double deltaSec) {
        //First update mChangePerSec, then update mValue
        //There is an ideal mChangePerSec for every distance. Move towards it.
        double distance = mTargetValue - mValue;
        double accel = distance * mAccelConst;
        mValue += mChangePerSec * deltaSec + accel*deltaSec*deltaSec/2.0;
        mChangePerSec += accel * deltaSec;
        mChangePerSec *= Math.pow(mAttenuation, deltaSec);
    }
}
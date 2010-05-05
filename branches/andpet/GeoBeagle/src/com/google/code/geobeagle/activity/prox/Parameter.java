package com.google.code.geobeagle.activity.prox;

/** Contains the value of a single parameter. This abstraction allows for 
 * animating arbitrary parameters smoothly to their current value. */
public class Parameter {

    /** If an initial value has been set */
    protected boolean mIsInited = false;
    /** Max change in movement towards target value, in delta / (sec^2) */
    protected double mValue;
    protected double mTargetValue;
    /** Delta per second for mValue. Updated to move mValue towards mTargetValue */
    protected double mChangePerSec = 0;

    /** How quickly the movement towards the correct value accelerates */
    protected final double mAccelConst;

    /** Part of the speed that is preserved after one second if otherwise unchanged. */
    protected final double mAttenuation;
    
    public Parameter(double accelConst, double attenuation, double init) {
        mAccelConst = accelConst;
        mAttenuation = attenuation;
        mTargetValue = init;
        mValue = init;
        mIsInited = true;
    }

    public Parameter(double accelConst, double attenuation) {
        mAccelConst = accelConst;
        mAttenuation = attenuation;
        mTargetValue = 0;
        mValue = 0;
        mIsInited = false;
    }
    
    public Parameter(double init) {
        mAccelConst = 0.8;
        mAttenuation = 0.3;
        mTargetValue = init;
        mValue = init;
        mIsInited = true;
    }
    
    public Parameter() {
        mAccelConst = 0.8;
        mAttenuation = 0.3;
        mTargetValue = 0;
        mValue = 0;
        mIsInited = false;
    }
    
    public double get() { 
        return mValue; 
    }

    public void set(double value) { 
        if (mIsInited) {
            mTargetValue = value; 
        } else {
            mTargetValue = value;
            mValue = value;
            mIsInited = true;
        }
    }

    //Override this!
    /** Animate the value towards its goal given that deltaSec time 
     * elapsed since last update */
    public void update(double deltaSec) { };
}
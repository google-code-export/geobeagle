package com.google.code.geobeagle.activity.prox;

/** Contains the value of a single parameter. This abstraction allows for 
 * animating arbitrary parameters smoothly to their current value. */
interface Parameter {
    public void update(double deltaSec);
    public double get();
    public void set(double value);
    //public void initialize(double value);   //Jumps to 'value' directly
}
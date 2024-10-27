package com.replog.common.model;

import java.text.DecimalFormat;

public class EndlessCounterState implements Comparable<EndlessCounterState> {
    private double real;
    private double imaginary;

    public EndlessCounterState() {
        this.real = 1.0;
        this.imaginary = 0.0; // angle 0 degrees
    }

    public EndlessCounterState(EndlessCounterState other) {
        this.real = other.real;
        this.imaginary = other.imaginary; // angle 0 degrees
    }

    public EndlessCounterState(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }

    public double getImaginary() {
        return imaginary;
    }

    public void setImaginary(double imaginary) {
        this.imaginary = imaginary;
    }


    public double getAngleDegreesCW() {
        double angleRadians = Math.atan2(imaginary, real);
        if (angleRadians < 0) {
            angleRadians += 2 * Math.PI;
        }
        double angleDegrees = Math.toDegrees(angleRadians);
        double angleDegreesCW = (360 - angleDegrees) % 360;
        return angleDegreesCW;
    }

    public String getAngleStr() {
        long angleRounded = Math.round(getAngleDegreesCW());
        return angleRounded + "°";
    }

    @Override
    public int compareTo(EndlessCounterState other) {
        double thisAngle = this.getAngleDegreesCW();
        double otherAngle = other.getAngleDegreesCW();

        if (thisAngle == otherAngle) {
            return 0;
        }

        double angleDiffThisToOther = (thisAngle - otherAngle) % 360;

        if (Math.abs(angleDiffThisToOther) < 0.01){
            return 0;
        }
        else if(angleDiffThisToOther > 0.0){
            return 1;
        }
        else{
            return -1;
        }
    }
}

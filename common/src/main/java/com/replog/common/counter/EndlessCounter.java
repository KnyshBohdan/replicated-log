package com.replog.common.counter;

import com.replog.common.model.EndlessCounterState;


public class EndlessCounter {
    private static final double DEFAULT_CIRCLE_RADIUS = 1.0;
    private static final double DEFAULT_ANGULAR_VELOCITY = 10.0; // degrees per increment

    private double circleRadius;
    private double angularVelocity; // in degrees per increment

    public EndlessCounter() {
        this(DEFAULT_CIRCLE_RADIUS, DEFAULT_ANGULAR_VELOCITY);
    }

    public EndlessCounter(double circleRadius, double angularVelocity) {
        if (circleRadius <= 0) {
            throw new IllegalArgumentException("Circle radius must be positive.");
        }
        if (angularVelocity <= 0) {
            throw new IllegalArgumentException("Angular velocity must be positive.");
        }
        this.circleRadius = circleRadius;
        this.angularVelocity = angularVelocity;
    }

    public void increment(EndlessCounterState state) {
        double angleDegreesCW = state.getAngleDegreesCW();

        double newAngleDegreesCW = (angleDegreesCW + angularVelocity) % 360;

        double newAngleDegrees = (360 - newAngleDegreesCW) % 360;

        double newAngleRadians = Math.toRadians(newAngleDegrees);

        double newReal = circleRadius * Math.cos(newAngleRadians);
        double newImaginary = circleRadius * Math.sin(newAngleRadians);

        state.setReal(newReal);
        state.setImaginary(newImaginary);
    }

    public double getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(double circleRadius) {
        if (circleRadius <= 0) {
            throw new IllegalArgumentException("Circle radius must be positive.");
        }
        this.circleRadius = circleRadius;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(double angularVelocity) {
        if (angularVelocity <= 0) {
            throw new IllegalArgumentException("Angular velocity must be positive.");
        }
        this.angularVelocity = angularVelocity;
    }
}


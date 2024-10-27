package com.replog.common.counter;

import com.replog.common.counter.EndlessCounter;
import com.replog.common.model.EndlessCounterState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EndlessCounterTest {
    private EndlessCounter counter;

    @BeforeEach
    public void setUp() {
        double circleRadius = 1.0;
        double angularVelocity = 45.0; // degrees per increment
        counter = new EndlessCounter(circleRadius, angularVelocity);
    }

    @Test
    public void testIncrement() {
        EndlessCounterState state = new EndlessCounterState(); // Defaults to (1, 0)

        // angle should be 0°
        Assertions.assertEquals("0°", state.getAngleStr());

        counter.increment(state);
        // angle should be 45°
        Assertions.assertEquals("45°", state.getAngleStr());

        counter.increment(state);
        // angle should be 90°
        Assertions.assertEquals("90°", state.getAngleStr());

        Assertions.assertEquals(0.0, state.getReal(), 0.0001);
        Assertions.assertEquals(-1.0, state.getImaginary(), 0.0001);
    }

    @Test
    public void testCompareTo() {
        EndlessCounterState state1 = new EndlessCounterState(); // 0°
        EndlessCounterState state2 = new EndlessCounterState(); // 0°

        counter.increment(state1);

        Assertions.assertTrue(state1.compareTo(state2) > 0);
        Assertions.assertTrue(state2.compareTo(state1) < 0);
        Assertions.assertEquals(0, state1.compareTo(state1));

        counter.increment(state2);

        Assertions.assertEquals(0, state1.compareTo(state2));

        for (int i = 0; i < 4; i++) {
            counter.increment(state1); // 90°, 135°, 180°, 225°
        }

        Assertions.assertTrue(state2.compareTo(state1) < 0); // 225 > 45: because in 180 degree
        Assertions.assertTrue(state1.compareTo(state2) > 0); // 225 < 45

        counter.increment(state1); // 270°

        Assertions.assertTrue(state2.compareTo(state1) > 0); // 275 < 45: because -45 < 0

        counter.increment(state2); // 90°
        counter.increment(state2); // 135°

        Assertions.assertTrue(state2.compareTo(state1) < 0); // 275 > 135
    }

    @Test
    public void testDefault() {
        EndlessCounter counterDefault = new EndlessCounter();
        EndlessCounterState state1 = new EndlessCounterState();
        EndlessCounterState state2 = new EndlessCounterState();

        counterDefault.increment(state1);
        Assertions.assertEquals("10°", state1.getAngleStr());
        Assertions.assertTrue(state1.compareTo(state2) > 0);
    }
}
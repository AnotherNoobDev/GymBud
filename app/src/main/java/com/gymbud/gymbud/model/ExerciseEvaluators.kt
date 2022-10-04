package com.gymbud.gymbud.model

/**
 * Epley formula: https://en.wikipedia.org/wiki/One-repetition_maximum
 */
fun calculateOneRepMax(reps: Int, resistance: Double): Double {
    assert(reps > 0)

    return if (reps == 1) {
        resistance
    } else {
        resistance * (1 + reps / 30.0)
    }
}
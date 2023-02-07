package com.gymbud.gymbud.model

/**
 * Epley formula: https://en.wikipedia.org/wiki/One-repetition_maximum
 */
fun calculateOneRepMax(reps: Int, resistance: Double): Double {
    return when {
        reps <= 0 -> 0.0
        reps == 1 -> resistance
        else -> resistance * (1 + reps / 30.0)
    }
}
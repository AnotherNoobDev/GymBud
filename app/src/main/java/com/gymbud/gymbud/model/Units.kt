package com.gymbud.gymbud.model

enum class WeightUnit {
    KG,
    LB
}


fun convertKGtoLB(v: Double): Double = v * 2.2


fun convertLBtoKG(v: Double): Double = v / 2.2
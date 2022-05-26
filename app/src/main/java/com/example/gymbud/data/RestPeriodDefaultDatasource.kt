package com.example.gymbud.data

import com.example.gymbud.model.RestPeriod

object RestPeriodDefaultDatasource {
    val rest30 = RestPeriod(
        ItemIdentifierGenerator.REST30_ID,
        "Rest 30s",
        IntRange(30, 30)
    )

    val rest60 = RestPeriod(
        ItemIdentifierGenerator.REST60_ID,
        "Rest 60s",
        IntRange(60, 60)
    )

    val rest60to120 = RestPeriod(
        ItemIdentifierGenerator.REST60_TO_120_ID,
        "Rest 1-2 min",
        IntRange(60,120)
    )

    // todo add remaining rest periods
}
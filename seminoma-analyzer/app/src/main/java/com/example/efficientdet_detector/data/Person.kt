package com.example.seminoma_analyzer.data




data class Person(
    var id: Int = -1, // default id is -1
    val keyPoints: List<KeyPoint>,
    val score: Float
)
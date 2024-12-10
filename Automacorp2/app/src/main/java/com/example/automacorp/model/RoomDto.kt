package com.example.automacorp.model

data class RoomDto(
    val id: Long,
    val name: String,
    val currentTemperature: Double?,
    val targetTemperature: Double?,
    val windows: List<WindowDto>?,
)

data class RoomCommandDto(
    val name: String,
    val currentTemperature: Double?,
    val targetTemperature: Double?,
    val floor: Int = 1,
    val buildingId: Long = -10
)
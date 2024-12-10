package com.example.automacorp.model

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.automacorp.service.ApiServices
import com.example.automacorp.service.ApiServices.roomsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomViewModel: ViewModel() {
    var room by mutableStateOf <RoomDto?>(null)
    val roomsState = MutableStateFlow(RoomList())

    fun findAll() {
        viewModelScope.launch(context = Dispatchers.IO) { // (1)
            runCatching { ApiServices.roomsApiService.findAll().execute() }
                .onSuccess {
                    val rooms = it.body() ?: emptyList()
                    roomsState.value = RoomList(rooms) // (2)
                }
                .onFailure {
                    it.printStackTrace()
                    roomsState.value = RoomList(emptyList(), it.stackTraceToString() ) // (3)
                }
        }
    }
    fun findRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.findById(id).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }

    }
    fun updateRoom(id: Long, roomDto: RoomDto) {
        val command = RoomCommandDto(
            name = roomDto.name,
            targetTemperature = roomDto.targetTemperature ?.let { Math.round(it * 10) /10.0 },
            currentTemperature = roomDto.currentTemperature,
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.updateRoom(id, command).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun createRoom(roomDto: RoomDto) {
        val command = RoomCommandDto(
            name = roomDto.name,
            targetTemperature = roomDto.targetTemperature?.let { Math.round(it * 10) / 10.0 },
            currentTemperature = roomDto.currentTemperature
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.createRoom(command).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun deleteRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching {
                roomsApiService.deleteRoom(id).execute()  // Make the DELETE request
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    room = null
                }
            }
        }
    }
}


    /*fun listWindows(roomId: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.listWindows(roomId).execute() }
                .onSuccess {
                    windows = it.body() ?: emptyList()
                }
                .onFailure {
                    it.printStackTrace()
                    windows = emptyList()
                }
        }
    }*/

    /*fun updateWindow(windowId: Long, windowDto: WindowDto) {
        val command = WindowCommandDto(
            name = windowDto.name,
            status = windowDto.status
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.windowsApiService.updateWindow(windowId, command).execute() }
                .onSuccess {
                    val updatedWindow = it.body()
                    windows = windows.map { window ->
                        if (window.id == windowId) updatedWindow else window
                    }
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }*/






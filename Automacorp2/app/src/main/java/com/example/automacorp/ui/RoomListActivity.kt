package com.example.automacorp.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.automacorp.AutomacorpTopAppBar
import com.example.automacorp.MainActivity
import com.example.automacorp.RoomActivity
import com.example.automacorp.RoomUpdateButton
import com.example.automacorp.model.RoomDto
import com.example.automacorp.model.RoomViewModel
import com.example.automacorp.service.ApiServices
import com.example.automacorp.ui.theme.AutomacorpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomListActivity : ComponentActivity() {

    companion object {
        const val ROOM_PARAM = "com.automacorp.room.attribute"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: RoomViewModel by viewModels()

        val openRoom: (id: Long) -> Unit = { id ->
            val intent = Intent(this, RoomActivity::class.java).apply {
                putExtra(ROOM_PARAM, id.toString())
            }
            startActivity(intent)
        }

        val navigateBack: () -> Unit = {
            startActivity(Intent(this, MainActivity::class.java))
        }
        val onRoomCreate: () -> Unit = {
            val roomNameInput = EditText(this).apply {
                hint = "Room Name"
                inputType = InputType.TYPE_CLASS_TEXT
            }

            val targetTemperatureInput = EditText(this).apply {
                hint = "Target Temperature"
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }

            val currentTemperatureInput = EditText(this).apply {
                hint = "Current Temperature"
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }

            val dialogView = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                addView(roomNameInput)
                addView(targetTemperatureInput)
                addView(currentTemperatureInput)
            }

            val show = AlertDialog.Builder(this)
                .setTitle("Create Room")
                .setView(dialogView)
                .setPositiveButton("Create") { _, _ ->
                    val roomName = roomNameInput.text.toString()
                    val targetTemperature =
                        targetTemperatureInput.text.toString().toDoubleOrNull() ?: 20.0
                    val currentTemperature =
                        currentTemperatureInput.text.toString().toDoubleOrNull() ?: 22.0

                    val newRoom = RoomDto(
                        id = System.currentTimeMillis(),
                        name = roomName,
                        currentTemperature = currentTemperature,
                        targetTemperature = targetTemperature,
                        windows = emptyList()
                    )

                    viewModel.createRoom(newRoom)

                    Toast.makeText(
                        baseContext,
                        "Room '${newRoom.name}' created successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(baseContext, MainActivity::class.java))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        setContent {
            AutomacorpTheme {
                val roomsState by viewModel.roomsState.asStateFlow().collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.findAll()
                }

                Scaffold(
                    topBar = { AutomacorpTopAppBar("Automacorp", navigateBack) },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = onRoomCreate,
                        ) {
                            Icon(Icons.Filled.Create, contentDescription = "Delete Room")
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        when {
                            roomsState.error != null -> {
                                Toast.makeText(
                                    this@RoomListActivity,
                                    "Error loading rooms: ${roomsState.error}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            roomsState.rooms.isEmpty() && roomsState.error == null -> {
                                Text("No rooms found") // Optional UI feedback
                            }
                            roomsState.rooms.isNotEmpty() -> {
                                RoomList(
                                    rooms = roomsState.rooms,
                                    openRoom = openRoom
                                )
                            }
                        }
                    }

                }

            }
        }
    }
}

@Composable
fun RoomItem(room: RoomDto, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.Gray),
    ) {
        Row(
            modifier = modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Target temperature: ${room.targetTemperature}°",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${room.currentTemperature ?: "?"}°",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxSize()
            )

        }
    }
}

@Composable
fun RoomList(
    rooms: List<RoomDto>,
    openRoom: (id: Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        items(rooms, key = { it.id }) { room ->
            RoomItem(
                room = room,
                modifier = Modifier.clickable { openRoom(room.id) }
            )
        }
    }
}








package com.example.automacorp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.automacorp.model.RoomDto
import com.example.automacorp.model.RoomViewModel
import com.example.automacorp.service.RoomService
import com.example.automacorp.service.RoomsApiService
import com.example.automacorp.ui.theme.AutomacorpTheme
import kotlin.math.round


class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)
        val viewModel: RoomViewModel by viewModels()

        val roomId = param?.toLong()
        if (roomId != null) {
            viewModel.findRoom(roomId)
        }

        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }

        val onRoomSave: () -> Unit = {
            viewModel.room?.let { room ->
                viewModel.updateRoom(room.id, room)
                Toast.makeText(baseContext, "Room ${room.name} was updated", Toast.LENGTH_LONG).show()
                startActivity(Intent(baseContext, MainActivity::class.java))
            }
        }

        val onRoomDelete: () -> Unit = {
            viewModel.room?.let { room ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Room")
                    .setMessage("Are you sure you want to delete the room '${room.name}'?")
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.deleteRoom(room.id)
                        Toast.makeText(baseContext, "Room deleted successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(baseContext, MainActivity::class.java))  // Redirect after deletion
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }

        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Automacorp", navigateBack) },
                    floatingActionButton = {
                        // FloatingActionButton for updating room
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            RoomUpdateButton(onRoomSave)

                            FloatingActionButton(
                                onClick = onRoomDelete,
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Room")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (viewModel.room != null) {
                        RoomDetail(viewModel, Modifier.padding(innerPadding))
                    } else {
                        NoRoom(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun RoomUpdateButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.create_room_message),
            )
        },
        text = { Text(text = stringResource(R.string.create_room_message)) }
    )
}

@Composable
fun NoRoom(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.act_room_none),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RoomDetail(model: RoomViewModel, modifier: Modifier = Modifier) {
    val room = model.room

    if (room != null) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.act_room_name),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = model.room?.name ?: "",
                onValueChange = {
                    model.room = model.room?.copy(name = it)
                },
                label = { Text(text = stringResource(R.string.act_room_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.act_room_current_temperature),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "${model.room?.currentTemperature} °C",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.act_room_target_temperature),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Slider(
                value = model.room?.targetTemperature?.toFloat() ?: 18.0f,
                onValueChange = { model.room = model.room?.copy(targetTemperature = it.toDouble()) },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                steps = 0,
                valueRange = 10f..28f
            )
            Text(text = (round((model.room?.targetTemperature ?: 18.0) * 10) / 10).toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomDetailPreview() {
    AutomacorpTheme {
        val mockRoomViewModel = RoomViewModel().apply{
            room= RoomDto(90, "Class", 19.5,21.0, windows =null)
        }
        RoomDetail(mockRoomViewModel)
        NoRoom()
    }
}



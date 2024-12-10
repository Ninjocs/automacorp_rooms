package com.example.automacorp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.automacorp.service.RoomService
import com.example.automacorp.ui.theme.AutomacorpTheme

class RoomDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get room ID from the intent
        val roomId = intent.getLongExtra("ROOM_ID", -1L) // Use getLongExtra instead of getStringExtra
        if (roomId == -1L) {
            // Handle case where the room ID is not found
            Toast.makeText(this, "Room not found!", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if room ID is invalid
        } else {
            setContent {
                AutomacorpTheme {
                    RoomDetailScreen(roomId = roomId)
                }
            }
        }
    }
}

@Composable
fun RoomDetailScreen(roomId: Long) {
    val room = RoomService.findById(roomId) // Use the room ID to get the correct room

    if (room != null) {
        Text(
            text = "Room Details for: ${room.name}\n" +
                    "Current Temperature: ${room.currentTemperature}°\n" +
                    "Target Temperature: ${room.targetTemperature}°",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        Text(
            text = "Room details not available.",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomDetailScreenPreview() {
    AutomacorpTheme {
        RoomDetailScreen(roomId = 1)
    }
}
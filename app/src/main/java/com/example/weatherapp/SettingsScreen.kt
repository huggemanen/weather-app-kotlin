package com.example.weatherapp

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit

// Hämtar inställningar från sharedPreferences
fun getSettings(context: Context): SharedPreferences {
    return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
}


@Composable
// Visar inställningsskärmen som navigeras till via navbaren i botten av skärmen
fun SettingsScreen() {
    val context = LocalContext.current
    val settings = getSettings(context)
    val useCelsius = settings.getBoolean("useCelsius", true)
    val useMetric = settings.getBoolean("useMetric", true)
    val use24Hour = settings.getBoolean("use24Hour", true)

    val tempOptions = listOf("Celsius (°C)", "Fahrenheit (°F)")
    val unitOptions = listOf("Metric (kph)", "Imperial (mph)")
    val clockOptions = listOf("24 hour clock", "12 hour clock")

    var selectedTemp by remember { mutableStateOf(if (useCelsius) tempOptions[0] else tempOptions[1]) }
    var selectedUnit by remember { mutableStateOf(if (useMetric) unitOptions[0] else unitOptions[1]) }
    var selectedClock by remember { mutableStateOf( if (use24Hour) clockOptions[0] else clockOptions[1] ) }

    var startTemp = remember { selectedTemp }
    var startUnit = remember { selectedUnit }
    var startClock = remember {selectedClock}

    var unsavedChanges by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF6082B6))
                .padding(top = 16.dp, start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column() {
                Text(
                    text = "Settings",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Change your local settings",
                    style = TextStyle(
                        color = Color.White
                    )
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Temperature unit",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                tempOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == selectedTemp),
                                onClick = {
                                    if (option != selectedTemp) {
                                        if (startTemp != option) {
                                            unsavedChanges++
                                        } else {
                                            unsavedChanges--
                                        }
                                        selectedTemp = option
                                    }
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (option == selectedTemp) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Celsius or Fahrenheit",
                            tint = Color.White
                        )
                        Text(
                            modifier = Modifier.padding(start=8.dp),
                            text = option,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Unit system",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                unitOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == selectedUnit),
                                onClick = {
                                    if (option != selectedUnit) {
                                        if (startUnit != option) {
                                            unsavedChanges++
                                        } else {
                                            unsavedChanges--
                                        }
                                        selectedUnit = option
                                    }
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (option == selectedUnit) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Metric or Imperial",
                            tint = Color.White
                        )
                        Text(
                            modifier = Modifier.padding(start=8.dp),
                            text = option,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Clock type",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                clockOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == selectedClock),
                                onClick = {
                                    if (option != selectedClock) {
                                        if (startClock != option) {
                                            unsavedChanges++
                                        } else {
                                            unsavedChanges--
                                        }
                                        selectedClock = option
                                    }
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (option == selectedClock) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Metric or Imperial",
                            tint = Color.White
                        )
                        Text(
                            modifier = Modifier.padding(start=8.dp),
                            text = option,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.weight(1f),
                enabled = (unsavedChanges > 0),
                onClick = {
                    selectedTemp = startTemp
                    selectedUnit = startUnit
                    selectedClock = startClock

                    unsavedChanges = 0
                }
            ) {
                Text("Cancel")
            }

            Button(
                modifier = Modifier.weight(1f),
                enabled = (unsavedChanges > 0),
                onClick = {
                    settings.edit {
                        putBoolean("useCelsius", (selectedTemp == tempOptions[0]))
                        putBoolean("useMetric", (selectedUnit == unitOptions[0]))
                        putBoolean("use24Hour", (selectedClock == clockOptions[0]))
                    }

                    startTemp = selectedTemp
                    startUnit = selectedUnit
                    startClock = selectedClock

                    unsavedChanges = 0
                }
            ) {
                Text("Save")
            }
        }
    }
}
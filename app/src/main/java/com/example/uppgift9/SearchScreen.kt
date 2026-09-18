package com.example.uppgift9

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.edit
import androidx.navigation.NavController

@Composable
// Visar sökskärmen som navigeras till via navbaren i botten av skärmen
// Visar resultat som autocomplete när man skriver en plats i sökfältet
// Kan favoritmarkera och trycka på platsen för att se väderdata
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    val settings = getSettings(context)
    var favorites by remember { mutableStateOf( (settings.getStringSet("favorites", null) ?: emptySet())) }

    var searchString by remember { mutableStateOf("") }
    var showInfoBox by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf(listOf<SearchLocation>()) }

    val api = Api()

    if (showInfoBox) {
        AlertDialog(
            onDismissRequest = { showInfoBox = false },
            title = { Text("Search info") },
            text = { Text("Due to technical restrictions, you may have to type the full name of the desired location for it to appear among the results.") },
            confirmButton = {
                TextButton(onClick = { showInfoBox = false }) {
                    Text("Ok")
                }
            }
        )
    }

    if (searchString.isNotEmpty()) {
        api.getSearchLocations(searchString) { locations ->
            searchResults = locations ?: emptyList()
        }
    } else {
        searchResults = emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            label = {Text("Search for a location")},
            value = searchString,
            onValueChange = {searchString = it},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = { showInfoBox = true }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Outlined.Info,
                        tint = Color.Black,
                        contentDescription = "Info",

                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF6082B6))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchResults) { location ->

                val locationIdString = location.id.toString()
                val isFavorite = favorites.contains(locationIdString)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                navController.navigate("details/${location.id}")
                            }
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column() {
                        Text(
                            text = location.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        val regionString = if (location.region == "") location.country else "${location.region}, ${location.country}"

                        Text(
                            text = regionString,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            style = TextStyle(
                                color = Color.White
                            )
                        )
                    }

                    Column() {
                        IconButton(
                            onClick = {
                                favorites = if (isFavorite) {
                                    favorites - locationIdString
                                } else {
                                    favorites + locationIdString
                                }

                                settings.edit {
                                    putStringSet("favorites", favorites)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                    }

                }
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color(0xFF345E9D))
                )
            }
        }
    }


}
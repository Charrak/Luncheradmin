package com.example.seniorenbeheerder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.seniorenbeheerder.tr
import com.example.seniorenbeheerder.SeniorenViewModel
import com.example.seniorenbeheerder.data.SeniorState
import com.example.seniorenbeheerder.ui.theme.SeniorenBeheerderTheme

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun DashboardScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    DashboardContent(
        state = viewModel.state,
        onUpdatePhoneNumber = viewModel::updatePhoneNumber,
        onSendCommand = viewModel::sendCommand,
        onHandleIncomingSms = viewModel::handleIncomingSms,
        modifier = modifier
    )
}

@Composable
fun DashboardContent(
    state: SeniorState,
    onUpdatePhoneNumber: (String) -> Unit,
    onSendCommand: (String) -> Unit,
    onHandleIncomingSms: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val locationMarkerTitle = tr("Locatie Senior", "Standort der betreuten Person")
    var showPhoneDialog by remember { mutableStateOf(false) }
    var phoneNumberInput by remember { mutableStateOf(state.phoneNumber) }
    var popupMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // High-end Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        tr("Beheer van Senior", "Betreute Person"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (state.phoneNumber.isBlank()) tr("Niet ingesteld", "Nicht eingerichtet") else state.phoneNumber,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row {
                    FilledIconButton(
                        onClick = { showPhoneDialog = true },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = tr("Bewerken", "Bearbeiten"))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onSendCommand("#STATUS") },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sync")
                    }
                }
            }
        }

        if (state.isSyncing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
        }

        // Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                tr("Batterij", "Akku"),
                "${state.batteryLevel}%",
                if (state.batteryLevel > 20) Icons.Outlined.BatteryFull else Icons.Outlined.BatteryAlert,
                if (state.batteryLevel > 20) MaterialTheme.colorScheme.primary else Color.Red,
                Modifier.weight(1f)
            )
            StatusCard(
                tr("Netwerk", "Netz"),
                tr("Signaal", "Signal"),
                Icons.Outlined.NetworkCheck,
                MaterialTheme.colorScheme.secondary,
                Modifier.weight(1f),
                onClick = { onSendCommand("#NETWERK") }
            )
            StatusCard(
                tr("Volume", "Lautstärke"),
                "${state.volumeLevel}/15",
                Icons.Outlined.VolumeUp,
                MaterialTheme.colorScheme.tertiary,
                Modifier.weight(1f)
            )
        }

        // Section: Location
        SectionHeader(tr("Locatie & Veiligheid", "Standort & Sicherheit"))
        
        if (state.latitude != null && state.longitude != null) {
            val geoPoint = GeoPoint(state.latitude, state.longitude)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Box(Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { context ->
                            MapView(context).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(16.0)
                                controller.setCenter(geoPoint)
                                
                                val marker = Marker(this)
                                marker.position = geoPoint
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title = locationMarkerTitle
                                overlays.add(marker)

                                setOnTouchListener { v, _ ->
                                    v.parent.requestDisallowInterceptTouchEvent(true)
                                    false
                                }
                            }
                        },
                        update = { view ->
                            view.controller.animateTo(geoPoint)
                            val marker = view.overlays.filterIsInstance<Marker>().firstOrNull()
                            marker?.position = geoPoint
                            view.invalidate()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        tonalElevation = 4.dp
                    ) {
                        IconButton(onClick = { onHandleIncomingSms("Locatie: ") }) {
                            Icon(Icons.Default.Close, contentDescription = tr("Sluiten", "Schließen"), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        } else {
            OutlinedActionTile(
                title = tr("Locatie Opvragen", "Standort abfragen"),
                subtitle = tr("Vraag de huidige GPS positie op", "Aktuelle GPS-Position anfordern"),
                icon = Icons.Outlined.LocationOn,
                onClick = { onSendCommand("#WAAR") }
            )
        }

        // Quick Actions
        SectionHeader(tr("Directe Interactie", "Schnellaktionen"))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ModernActionButton(
                text = tr("Roepen", "Gerät suchen"),
                icon = Icons.Outlined.NotificationsActive,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f),
                onClick = { onSendCommand("LAUN_ZOEK") }
            )
            ModernActionButton(
                text = tr("Bel mij", "Rückruf"),
                icon = Icons.Outlined.Call,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f),
                onClick = { onSendCommand("#BEL_TERUG") }
            )
        }

        // Tools Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(tr("Hulpmiddelen", "Werkzeuge"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(tr("Zaklamp", "Taschenlampe"), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    
                    Row {
                        FilledTonalIconButton(onClick = { onSendCommand("#LAMP ON") }) {
                            Icon(Icons.Default.FlashlightOn, contentDescription = tr("Aan", "Ein"))
                        }
                        Spacer(Modifier.width(4.dp))
                        FilledTonalIconButton(onClick = { onSendCommand("#LAMP OFF") }) {
                            Icon(Icons.Default.FlashlightOff, contentDescription = tr("Uit", "Aus"))
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(tr("Bericht op scherm", "Nachricht auf dem Bildschirm"), style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = popupMessage,
                        onValueChange = { popupMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(tr("Typ een bericht voor de senior...", "Nachricht an die betreute Person ...")) },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { 
                                onSendCommand("#BERICHT $popupMessage")
                                popupMessage = ""
                            }) {
                                Icon(Icons.Default.Send, contentDescription = tr("Verstuur", "Senden"), tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }

    // Dialogs (unchanged logic, updated visuals)
    if (showPhoneDialog) {
        AlertDialog(
            onDismissRequest = { showPhoneDialog = false },
            title = { Text(tr("Instellen telefoonnummer", "Telefonnummer einrichten")) },
            text = {
                OutlinedTextField(
                    value = phoneNumberInput,
                    onValueChange = { phoneNumberInput = it },
                    label = { Text(tr("Telefoonnummer Senior", "Telefonnummer der betreuten Person")) },
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    onUpdatePhoneNumber(phoneNumberInput)
                    showPhoneDialog = false
                }) {
                    Text(tr("Opslaan", "Speichern"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPhoneDialog = false }) {
                    Text(tr("Annuleren", "Abbrechen"))
                }
            }
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusCard(title: String, value: String, icon: ImageVector, iconColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ModernActionButton(text: String, icon: ImageVector, containerColor: Color, contentColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        contentPadding = PaddingValues(12.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedActionTile(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    SeniorenBeheerderTheme {
        DashboardContent(
            state = SeniorState(
                phoneNumber = "+31612345678",
                batteryLevel = 85,
                volumeLevel = 10
            ),
            onUpdatePhoneNumber = {},
            onSendCommand = {},
            onHandleIncomingSms = {}
        )
    }
}

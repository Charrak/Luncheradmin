package com.example.seniorenbeheerder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seniorenbeheerder.tr
import com.example.seniorenbeheerder.SeniorenViewModel
import kotlin.math.roundToInt

import androidx.compose.ui.tooling.preview.Preview
import com.example.seniorenbeheerder.data.SeniorState
import com.example.seniorenbeheerder.ui.theme.SeniorenBeheerderTheme

@Composable
fun SettingsScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    SettingsContent(
        state = viewModel.state,
        onSendCommand = viewModel::sendCommand,
        onLanguageChange = viewModel::updateLanguage,
        onSaveRecipientPin = viewModel::updateRecipientPin,
        onPinProtectionChange = viewModel::setPinProtection,
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    state: SeniorState,
    onSendCommand: (String) -> Unit,
    onLanguageChange: (String) -> Unit = {},
    onSaveRecipientPin: (String) -> Unit = {},
    onPinProtectionChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var recipientPinInput by remember(state.recipientPin) { mutableStateOf(state.recipientPin) }
    var localVolume by remember(state.volumeLevel) { mutableStateOf(state.volumeLevel.toFloat()) }
    var localMediaVolume by remember(state.mediaVolumeLevel) { mutableStateOf(state.mediaVolumeLevel.toFloat()) }
    var localBrightness by remember(state.brightnessLevel) { mutableStateOf(state.brightnessLevel.toFloat()) }
    var localTextSize by remember(state.brightnessLevel) { mutableStateOf(3f) } // Placeholder

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    
    var showOpenAppDialog by remember { mutableStateOf(false) }
    var appNameInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(tr("Toestel Instellingen", "Geräteeinstellungen"), style = MaterialTheme.typography.headlineMedium)

        SettingsCard(title = tr("Taal", "Sprache"), icon = Icons.Default.Language) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("de" to "Deutsch", "nl" to "Nederlands").forEach { (language, label) ->
                    FilterChip(selected = state.language == language, onClick = { onLanguageChange(language) }, label = { Text(label) })
                }
            }
        }

        SettingsCard(title = tr("SMS-PIN-beveiliging", "SMS-PIN-Schutz"), icon = Icons.Default.Lock) {
            Text(tr("Gebruik dezelfde viercijferige PIN als voor de instellingen op het ontvangende toestel.", "Verwenden Sie dieselbe vierstellige PIN wie für die Einstellungen auf dem Empfangsgerät."))
            OutlinedTextField(
                value = recipientPinInput,
                onValueChange = { value -> if (value.length <= 4 && value.all { it in '0'..'9' }) recipientPinInput = value },
                label = { Text(tr("PIN van ontvanger", "PIN des Empfängers")) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { onSaveRecipientPin(recipientPinInput) }, enabled = recipientPinInput.length == 4) {
                Text(tr("PIN opslaan", "PIN speichern"))
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(tr("PIN voor SMS gebruiken", "PIN für SMS verwenden"), modifier = Modifier.weight(1f))
                Switch(checked = state.pinProtectionEnabled, enabled = state.pinProtectionEnabled || state.recipientPin.length == 4, onCheckedChange = onPinProtectionChange)
            }
            Text(tr("Schakel de beveiliging ook op het ontvangende toestel in of uit. SMS is niet versleuteld; de PIN staat lokaal opgeslagen.", "Schalten Sie den Schutz auch auf dem Empfangsgerät ein oder aus. SMS sind unverschlüsselt; die PIN wird lokal gespeichert."), style = MaterialTheme.typography.bodySmall)
        }

        // Volume Sliders
        SettingsCard(title = tr("Volume Beltoon", "Klingeltonlautstärke"), icon = Icons.Default.VolumeUp) {
            Slider(
                value = localVolume,
                onValueChange = { localVolume = it },
                onValueChangeFinished = { onSendCommand("#VOLUME ${localVolume.roundToInt()}") },
                valueRange = 0f..15f,
                steps = 14
            )
            Text("${localVolume.roundToInt()}/15", modifier = Modifier.align(Alignment.End))
        }

        SettingsCard(title = tr("Media Volume", "Medienlautstärke"), icon = Icons.Default.MusicNote) {
            Slider(
                value = localMediaVolume,
                onValueChange = { localMediaVolume = it },
                onValueChangeFinished = { onSendCommand("#VOLUME_MEDIA ${localMediaVolume.roundToInt()}") },
                valueRange = 0f..10f,
                steps = 9
            )
            Text("${localMediaVolume.roundToInt()}/10", modifier = Modifier.align(Alignment.End))
        }

        SettingsCard(title = tr("Helderheid", "Helligkeit"), icon = Icons.Default.BrightnessMedium) {
            Slider(
                value = localBrightness,
                onValueChange = { localBrightness = it },
                onValueChangeFinished = { onSendCommand("#HELDER ${localBrightness.roundToInt()}") },
                valueRange = 1f..10f,
                steps = 8
            )
            Text("${localBrightness.roundToInt()}/10", modifier = Modifier.align(Alignment.End))
        }

        // Toggles
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                ToggleRow(
                    label = "Wi-Fi",
                    checked = state.isWifiEnabled,
                    onCheckedChange = { onSendCommand("#WIFI ${if (it) "AAN" else "UIT"}") }
                )
                HorizontalDivider()
                ToggleRow(
                    label = "Bluetooth",
                    checked = state.isBluetoothEnabled,
                    onCheckedChange = { onSendCommand("#BT ${if (it) "AAN" else "UIT"}") }
                )
                HorizontalDivider()
                ToggleRow(
                    label = tr("Stille Modus", "Lautlosmodus"),
                    checked = state.isSilentMode,
                    onCheckedChange = { onSendCommand("#STIL ${if (it) "AAN" else "UIT"}") }
                )
            }
        }

        Text(tr("UI & Weergave", "Anzeige"), style = MaterialTheme.typography.titleMedium)
        
        SettingsCard(title = tr("Tekstgrootte", "Schriftgröße"), icon = Icons.Default.TextFormat) {
            Slider(
                value = localTextSize,
                onValueChange = { localTextSize = it },
                onValueChangeFinished = { onSendCommand("#LETTER ${localTextSize.roundToInt()}") },
                valueRange = 1f..5f,
                steps = 3
            )
            val labels = listOf(tr("Klein", "Klein"), tr("Normaal", "Normal"), tr("Groot", "Groß"), tr("Extra Groot", "Sehr groß"), tr("Enorm", "Riesig"))
            Text(labels[localTextSize.roundToInt() - 1], modifier = Modifier.align(Alignment.End))
        }

        SettingsCard(title = tr("Scherm Timeout", "Bildschirm-Timeout"), icon = Icons.Default.Timer) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1", "2", "5", "MAX").forEach { min ->
                    OutlinedButton(
                        onClick = { onSendCommand("#SCHERM_TIJD $min") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${min}m")
                    }
                }
            }
        }

        Text(tr("App Management", "App-Verwaltung"), style = MaterialTheme.typography.titleMedium)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showOpenAppDialog = true }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(tr("App Openen", "App öffnen"))
                }
                Button(onClick = { onSendCommand("#APP_LIJST") }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(tr("App Lijst", "App-Liste"))
                }
            }
            Button(
                onClick = { onSendCommand("#NOTIFICATIES_WEG") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.NotificationsOff, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(tr("Notificaties opschonen", "Benachrichtigungen löschen"))
            }
        }

        Text(tr("Systeem & Beveiliging", "System & Sicherheit"), style = MaterialTheme.typography.titleMedium)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showPinDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(tr("PIN Code wijzigen", "PIN des Empfängers ändern"))
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSendCommand("#UPDATE_CHECK") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Default.SystemUpdate, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(tr("Check Update", "Update prüfen"))
                }
                Button(
                    onClick = { onSendCommand("#RESTART") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(tr("Herstart App", "App neu starten"))
                }
            }
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text(tr("PIN wijzigen", "Empfänger-PIN ändern")) },
            text = {
                Column {
                Text(tr("De nieuwe PIN pas lokaal opslaan nadat de ontvanger de wijziging heeft bevestigd.", "Die neue PIN erst lokal speichern, nachdem der Empfänger die Änderung bestätigt hat."))
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c in '0'..'9' }) pinInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text(tr("Nieuwe 4-cijferige PIN", "Neue vierstellige PIN")) },
                    placeholder = { Text(tr("Bijv: 1234", "Z. B. 1234")) }
                )
                }
            },
            confirmButton = {
                Button(enabled = pinInput.length == 4, onClick = {
                    onSendCommand("#PIN $pinInput")
                    showPinDialog = false
                    pinInput = ""
                }) {
                    Text(tr("Opslaan", "Speichern"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text(tr("Annuleren", "Abbrechen"))
                }
            }
        )
    }

    if (showOpenAppDialog) {
        AlertDialog(
            onDismissRequest = { showOpenAppDialog = false },
            title = { Text(tr("App openen op afstand", "App aus der Ferne öffnen")) },
            text = {
                OutlinedTextField(
                    value = appNameInput,
                    onValueChange = { appNameInput = it },
                    label = { Text(tr("Naam van de app", "Name der App")) },
                    placeholder = { Text(tr("Bijv: Photos of Camera", "Z. B. Fotos oder Kamera")) }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onSendCommand("#OPEN $appNameInput")
                    showOpenAppDialog = false
                    appNameInput = ""
                }) {
                    Text(tr("Open", "Öffnen"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showOpenAppDialog = false }) {
                    Text(tr("Annuleren", "Abbrechen"))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SeniorenBeheerderTheme {
        SettingsContent(
            state = SeniorState(
                volumeLevel = 10,
                isWifiEnabled = true,
                isSilentMode = false
            ),
            onSendCommand = {}
        )
    }
}


@Composable
fun SettingsCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

package com.example.seniorenbeheerder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.seniorenbeheerder.tr
import com.example.seniorenbeheerder.SeniorenViewModel

import androidx.compose.ui.tooling.preview.Preview
import com.example.seniorenbeheerder.data.SeniorState
import com.example.seniorenbeheerder.ui.theme.SeniorenBeheerderTheme

@Composable
fun SafetyScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    SafetyContent(
        state = viewModel.state,
        onSendCommand = viewModel::sendCommand,
        onHandleIncomingSms = viewModel::handleIncomingSms,
        modifier = modifier
    )
}

@Composable
fun SafetyContent(
    state: SeniorState,
    onSendCommand: (String) -> Unit,
    onHandleIncomingSms: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSosDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var blockNumberInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(tr("Veiligheid & Beveiliging", "Sicherheit & Schutz"), style = MaterialTheme.typography.headlineMedium)

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleRow(
                    label = tr("Anti-Scam Modus", "Betrugsschutz"),
                    checked = state.antiScamEnabled,
                    onCheckedChange = { onSendCommand("#VEILIG ${if (it) "AAN" else "UIT"}") }
                )
                Text(
                    tr("Blokkeert automatisch oproepen van onbekende nummers die niet in de contactenlijst staan.", "Blockiert automatisch Anrufe unbekannter Nummern, die nicht in den Kontakten stehen."),
                    style = MaterialTheme.typography.bodySmall
                )
                
                Button(
                    onClick = { showBlockDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Block, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(tr("Specifiek nummer blokkeren", "Bestimmte Nummer blockieren"))
                }
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleRow(
                    label = tr("Instellingen Vergrendelen", "Einstellungen sperren"),
                    checked = state.settingsLocked,
                    onCheckedChange = { onSendCommand("#SLOT ${if (it) "AAN" else "UIT"}") }
                )
                Text(
                    tr("Voorkomt dat de senior per ongeluk belangrijke systeeminstellingen wijzigt.", "Verhindert versehentliche Änderungen wichtiger Systemeinstellungen."),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text(tr("Hulp & Welzijn op afstand", "Hilfe & Wohlbefinden aus der Ferne"), style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onSendCommand("#PING") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(tr("Vraag: 'Gaat het?'", "Fragen: 'Geht es dir gut?'"))
            }
            Button(
                onClick = { onSendCommand("#SPEAKER") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(tr("Forceer Luidspreker", "Lautsprecher aktivieren"))
            }
        }

        Text(tr("Diagnose & Systeem", "Diagnose & System"), style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onSendCommand("#PRIVACY") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Shield, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(tr("Privacy Status", "Datenschutzstatus"))
            }
            Button(
                onClick = { onSendCommand("#INFO_PLUS") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(tr("Info Plus", "Info Plus"))
            }
        }

        Button(
            onClick = { onSendCommand("#LAATSTE_OPROEP") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Call, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(tr("Details laatste oproep", "Details zum letzten Anruf"))
        }

        // Weergave van resultaten
        state.privacyReport?.let { report ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(tr("Privacy Rapport:", "Datenschutzbericht:"), style = MaterialTheme.typography.titleSmall)
                    Text(report, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = { onHandleIncomingSms("Privacy Status: ") }, modifier = Modifier.align(Alignment.End)) {
                        Text(tr("Sluiten", "Schließen"))
                    }
                }
            }
        }

        state.systemInfo?.let { info ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(tr("Systeem Informatie:", "Systeminformationen:"), style = MaterialTheme.typography.titleSmall)
                    Text(info, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = { onHandleIncomingSms("Info: ") }, modifier = Modifier.align(Alignment.End)) {
                        Text(tr("Sluiten", "Schließen"))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showSosDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            contentPadding = PaddingValues(20.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            Text(tr("FORCEER SOS NU", "SOS JETZT AUSLÖSEN"), style = MaterialTheme.typography.headlineSmall)
        }
    }

    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            title = { Text(tr("Bevestig SOS", "SOS bestätigen")) },
            text = { Text(tr("Weet u zeker dat u de SOS-modus op afstand wilt activeren? Dit zal noodcontacten waarschuwen en de sirene activeren.", "SOS aus der Ferne aktivieren? Dadurch werden Notfallkontakte benachrichtigt und die Sirene aktiviert.")) },
            confirmButton = {
                Button(
                    onClick = {
                        onSendCommand("#SOS_NU")
                        showSosDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(tr("JA, ACTIVEER", "JA, AKTIVIEREN"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSosDialog = false }) {
                    Text(tr("Annuleren", "Abbrechen"))
                }
            }
        )
    }

    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = { Text(tr("Nummer blokkeren", "Nummer blockieren")) },
            text = {
                OutlinedTextField(
                    value = blockNumberInput,
                    onValueChange = { blockNumberInput = it },
                    label = { Text(tr("Telefoonnummer", "Telefonnummer")) },
                    placeholder = { Text(tr("Bijv: 0484123456", "Z. B. 01701234567")) }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onSendCommand("#BLOKKEER $blockNumberInput")
                    showBlockDialog = false
                    blockNumberInput = ""
                }) {
                    Text(tr("Blokkeer", "Blockieren"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) {
                    Text(tr("Annuleren", "Abbrechen"))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SafetyPreview() {
    SeniorenBeheerderTheme {
        SafetyContent(
            state = SeniorState(
                antiScamEnabled = true,
                settingsLocked = false,
                privacyReport = "GPS: OK\nSMS: OK\nBEL: NEE"
            ),
            onSendCommand = {},
            onHandleIncomingSms = {}
        )
    }
}


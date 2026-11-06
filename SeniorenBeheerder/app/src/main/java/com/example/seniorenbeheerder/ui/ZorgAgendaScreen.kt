package com.example.seniorenbeheerder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seniorenbeheerder.tr
import com.example.seniorenbeheerder.SeniorenViewModel

import androidx.compose.ui.tooling.preview.Preview
import com.example.seniorenbeheerder.data.SeniorState
import com.example.seniorenbeheerder.ui.theme.SeniorenBeheerderTheme

@Composable
fun ZorgAgendaScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    ZorgAgendaContent(
        state = viewModel.state,
        onSendCommand = viewModel::sendCommand,
        onHandleIncomingSms = viewModel::handleIncomingSms,
        modifier = modifier
    )
}

@Composable
fun ZorgAgendaContent(
    state: SeniorState,
    onSendCommand: (String) -> Unit,
    onHandleIncomingSms: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var medicijnNaam by remember { mutableStateOf("") }
    var medicijnTijd by remember { mutableStateOf("08:00") }
    
    var voorraadNaam by remember { mutableStateOf("") }
    var voorraadAantal by remember { mutableStateOf("") }

    var agendaTekst by remember { mutableStateOf("") }
    var agendaDatum by remember { mutableStateOf("24-12") }
    var agendaTijd by remember { mutableStateOf("10:00") }

    var wekkerTijd by remember { mutableStateOf("07:30") }
    var wekkerLabel by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(tr("Zorg & Agenda", "Pflege & Termine"), style = MaterialTheme.typography.headlineMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onSendCommand("#AGENDA_VANDAAG") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(tr("Vandaag", "Heute"))
            }
            Button(
                onClick = { onSendCommand("#WEKKERS_LIJST") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Alarm, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(tr("Wekkers", "Wecker"))
            }
        }

        if (state.agendaToday.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(tr("Overzicht (Agenda/Wekkers):", "Übersicht (Termine/Wecker):"), style = MaterialTheme.typography.titleMedium)
                    state.agendaToday.forEach { item ->
                        Text("• $item", style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(
                        onClick = { onHandleIncomingSms("Agenda: ") },
                        modifier = Modifier.align(androidx.compose.ui.Alignment.End)
                    ) {
                        Text(tr("Wissen", "Löschen"))
                    }
                }
            }
        }

        // Medicatie
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(tr("Medicatie Toevoegen", "Medikament hinzufügen"), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = medicijnNaam, onValueChange = { medicijnNaam = it }, label = { Text(tr("Naam Medicijn", "Name des Medikaments")) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = medicijnTijd, onValueChange = { medicijnTijd = it }, label = { Text(tr("Tijd (bijv. 08:30)", "Uhrzeit (z. B. 08:30)")) }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { onSendCommand("#MEDICIJN $medicijnTijd $medicijnNaam") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text(tr("Remind", "Erinnerung setzen"))
                }
            }
        }

        // Voorraad
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(tr("Voorraad Bijwerken", "Vorrat aktualisieren"), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = voorraadNaam, onValueChange = { voorraadNaam = it }, label = { Text(tr("Naam Pil/Item", "Name des Medikaments")) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = voorraadAantal, onValueChange = { voorraadAantal = it }, label = { Text(tr("Aantal over", "Verbleibende Anzahl")) }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { onSendCommand("#VOORRAAD $voorraadNaam $voorraadAantal") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text(tr("Update", "Aktualisieren"))
                }
            }
        }

        // Agenda Afspraak
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(tr("Nieuwe Afspraak", "Neuer Termin"), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = agendaTekst, onValueChange = { agendaTekst = it }, label = { Text(tr("Wat: (bijv. Tandarts)", "Was: (z. B. Zahnarzt)")) }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = agendaDatum, onValueChange = { agendaDatum = it }, label = { Text(tr("Wanneer: (DD-MM)", "Wann: (TT-MM)")) }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = agendaTijd, onValueChange = { agendaTijd = it }, label = { Text(tr("Tijd: (HH:MM)", "Uhrzeit: (HH:MM)")) }, modifier = Modifier.weight(1f))
                }
                Button(onClick = { onSendCommand("#AGENDA $agendaDatum $agendaTijd $agendaTekst") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text(tr("Inplannen", "Einplanen"))
                }
            }
        }

        // Wekker
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(tr("Nieuwe Wekker", "Neuer Wecker"), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = wekkerTijd, onValueChange = { wekkerTijd = it }, label = { Text(tr("Tijd (HH:MM)", "Uhrzeit (HH:MM)")) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = wekkerLabel, onValueChange = { wekkerLabel = it }, label = { Text(tr("Label (bijv. Opstaan)", "Bezeichnung (z. B. Aufstehen)")) }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { onSendCommand("#WEKKER $wekkerTijd $wekkerLabel") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text(tr("Zet Wekker", "Wecker stellen"))
                }
            }
        }

        // Extra Tools
        Button(
            onClick = { onSendCommand("#RADIO_STOP") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
        ) {
            Icon(Icons.Default.Radio, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(tr("Stop Radio op afstand", "Radio aus der Ferne stoppen"))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ZorgAgendaPreview() {
    SeniorenBeheerderTheme {
        ZorgAgendaContent(
            state = SeniorState(
                agendaToday = listOf("09:00: Wandelen", "12:30: Lunch", "18:00: Medicatie")
            ),
            onSendCommand = {},
            onHandleIncomingSms = {}
        )
    }
}


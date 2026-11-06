package com.example.seniorenbeheerder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import com.example.seniorenbeheerder.ui.DashboardScreen
import com.example.seniorenbeheerder.ui.SafetyScreen
import com.example.seniorenbeheerder.ui.SettingsScreen
import com.example.seniorenbeheerder.ui.ZorgAgendaScreen
import com.example.seniorenbeheerder.ui.theme.SeniorenBeheerderTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: SeniorenViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SeniorenViewModel(this)
        
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                Configuration.getInstance().userAgentValue = "SeniorenBeheerder/1.0"
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            }
            SeniorenBeheerderTheme {
                CompositionLocalProvider(LocalLanguage provides viewModel.state.language) {
                    SetupHandler(viewModel)
                    SmsListener(viewModel)
                    SeniorenBeheerderApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun SetupHandler(viewModel: SeniorenViewModel) {
    val context = LocalContext.current
    var showPrivacyDialog by remember { mutableStateOf(!viewModel.state.isPrivacyAccepted) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showPhoneSetupDialog by remember { mutableStateOf(false) }

    val requiredPermissions = remember {
        mutableListOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val permissionMessage = tr("Machtigingen zijn essentieel voor de werking van de app.", "Berechtigungen sind für die App erforderlich.")
    val numberMessage = tr("Voer een geldig nummer in", "Bitte eine gültige Telefonnummer eingeben.")
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(context, permissionMessage, Toast.LENGTH_LONG).show()
        }
        // Check phone number after permissions
        if (viewModel.state.phoneNumber.isEmpty()) {
            showPhoneSetupDialog = true
        }
    }

    LaunchedEffect(viewModel.state.isPrivacyAccepted) {
        if (viewModel.state.isPrivacyAccepted) {
            val missingPermissions = requiredPermissions.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }

            if (missingPermissions.isNotEmpty()) {
                showPermissionDialog = true
            } else if (viewModel.state.phoneNumber.isEmpty()) {
                showPhoneSetupDialog = true
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(tr("Privacy & GDPR", "Datenschutz & DSGVO"), fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Sprache / Taal", fontWeight = FontWeight.Bold)
                    androidx.compose.foundation.layout.Row {
                        androidx.compose.material3.TextButton(onClick = { viewModel.updateLanguage("de") }) { Text("Deutsch") }
                        androidx.compose.material3.TextButton(onClick = { viewModel.updateLanguage("nl") }) { Text("Nederlands") }
                    }
                    Text(
                        tr("Welkom bij Senioren Beheerder. Wij hechten grote waarde aan uw privacy en die van de senior.", "Willkommen bei der Seniorenverwaltung. Ihre Privatsphäre und die der betreuten Person sind uns wichtig."),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tr("1. Gegevens: Instellingen blijven lokaal. De locatie wordt per SMS verstuurd en kaarttegels worden via internet geladen.", "1. Daten: Die App speichert Einstellungen lokal. Der Standort wird per SMS übertragen; Kartenkacheln werden über das Internet geladen."))
                    Text(tr("2. SMS: De app gebruikt SMS om commando's te sturen. Er kunnen kosten verbonden zijn aan het verzenden van SMS-berichten via uw provider.", "2. SMS: Die App sendet Befehle per SMS. Dabei können Kosten Ihres Mobilfunkanbieters entstehen."))
                    Text(tr("3. Locatie: Locatiegegevens worden alleen opgevraagd wanneer u daarom vraagt en worden direct op uw kaart getoond via Osmdroid (privacy-vriendelijk).", "3. Standort: Auf Anfrage wird der Standort per SMS übermittelt und auf einer Karte angezeigt."))
                    Text(tr("4. Rechten: U kunt uw gegevens op elk moment wissen door de app-gegevens te wissen in de instellingen.", "4. Daten löschen: Sie können die App-Daten jederzeit in den Android-Einstellungen löschen."))
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.acceptPrivacy()
                    showPrivacyDialog = false
                }) {
                    Text(tr("Ik ga akkoord", "Ich stimme zu"))
                }
            }
        )
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(tr("Machtigingen Nodig", "Berechtigungen erforderlich"), fontWeight = FontWeight.Bold) },
            text = { Text(tr("Voor een goede werking heeft deze app toegang nodig tot SMS (lezen/verzenden) en meldingen. Dit is nodig om de senior op afstand te beheren.", "Für die Fernsteuerung benötigt die App Zugriff auf SMS und Benachrichtigungen.")) },
            confirmButton = {
                Button(onClick = {
                    launcher.launch(requiredPermissions.toTypedArray())
                    showPermissionDialog = false
                }) {
                    Text(tr("Machtigingen Geven", "Berechtigungen erteilen"))
                }
            }
        )
    }

    if (showPhoneSetupDialog) {
        var tempNumber by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { /* Don't dismiss without action */ },
            title = { Text(tr("Telefoonnummer Instellen", "Telefonnummer einrichten"), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(tr("Voer het telefoonnummer in van de senior die u wilt beheren.", "Geben Sie die Telefonnummer der betreuten Person ein."))
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = tempNumber,
                        onValueChange = { tempNumber = it },
                        label = { Text(tr("Telefoonnummer", "Telefonnummer")) },
                        placeholder = { Text("+316...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempNumber.isNotEmpty()) {
                            viewModel.updatePhoneNumber(tempNumber)
                            showPhoneSetupDialog = false
                        } else {
                            Toast.makeText(context, numberMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(tr("Opslaan", "Speichern"))
                }
            }
        )
    }
}

@Composable
fun SmsListener(viewModel: SeniorenViewModel) {
    val context = LocalContext.current
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("MainActivity", "Received SMS_UPDATED broadcast")
                val body = intent?.getStringExtra("body") ?: ""
                viewModel.handleIncomingSms(body)
            }
        }
        val filter = IntentFilter("com.example.seniorenbeheerder.SMS_UPDATED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}

@Composable
fun SeniorenBeheerderApp(viewModel: SeniorenViewModel) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestinations.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination },
                        icon = { Icon(destination.icon, contentDescription = destination.label()) },
                        label = { Text(destination.label()) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (currentDestination) {
            AppDestinations.DASHBOARD -> DashboardScreen(viewModel, modifier)
            AppDestinations.ZORG -> ZorgAgendaScreen(viewModel, modifier)
            AppDestinations.INSTELLINGEN -> SettingsScreen(viewModel, modifier)
            AppDestinations.VEILIGHEID -> SafetyScreen(viewModel, modifier)
        }
    }
}

enum class AppDestinations(val nl: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Home),
    ZORG("Zorg", Icons.Default.DateRange),
    INSTELLINGEN("Instellingen", Icons.Default.Settings),
    VEILIGHEID("Veiligheid", Icons.Default.Lock);

    @Composable fun label(): String = when (this) {
        DASHBOARD -> tr(nl, "Übersicht")
        ZORG -> tr(nl, "Pflege")
        INSTELLINGEN -> tr(nl, "Einstellungen")
        VEILIGHEID -> tr(nl, "Sicherheit")
    }
}

val LocalLanguage = staticCompositionLocalOf { "de" }
@Composable fun tr(nl: String, de: String): String = if (LocalLanguage.current == "nl") nl else de

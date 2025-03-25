package proxy.kunkka.tts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import proxy.kunkka.tts.ui.theme.TTSGoTheme

import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.UpdateFrom
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.Locale
import android.widget.Toast

class MainActivity : ComponentActivity() {
    private lateinit var textToSpeech: TextToSpeech
    private var availableLanguages = mutableStateOf<List<Locale>>(emptyList())
    private var selectedLanguage = mutableStateOf<Locale?>(null)
    //https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize TTS
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                availableLanguages.value = textToSpeech.availableLanguages.toList()
                selectedLanguage.value = textToSpeech.language // Get current language
                // Set content after TTS initialization
                setContent {
                    TTSGoTheme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            TTSContent(
                                modifier = Modifier.padding(innerPadding),
                                languages = availableLanguages.value,
                                selectedLanguage = selectedLanguage.value,
                                onLanguageSelected = { locale ->
                                    selectedLanguage.value = locale
                                    textToSpeech.language = locale
                                },
                                onSpeak = { speakNumbers() }
                            )
                        }
                    }
                }
            }
        }

        try {
            AppUpdater(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json")
                .start()
        } catch (e: Exception) {
            // Log the error
            e.printStackTrace()
            // Show a toast message with error information
            Toast.makeText(this, "Failed to check for updates: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    private fun speakNumbers() {
        for (i in 21111..91111) {
            textToSpeech.speak(
                i.toString(),
                TextToSpeech.QUEUE_ADD,
                null,
                "$i"
            )
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }
}

@Composable
fun TTSContent(
    modifier: Modifier = Modifier,
    languages: List<Locale>,
    selectedLanguage: Locale?,
    onLanguageSelected: (Locale) -> Unit,
    onSpeak: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onSpeak) {
            Text("Start Counting")
        }

        Text(
            text = "Select Language:",
            style = MaterialTheme.typography.titleMedium
        )

        LanguageSelector(
            languages = languages,
            selectedLanguage = selectedLanguage,
            onLanguageSelected = onLanguageSelected
        )
    }
}

@Composable
fun LanguageSelector(
    languages: List<Locale>,
    selectedLanguage: Locale?,
    onLanguageSelected: (Locale) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedLanguage?.displayName ?: "Select a language"
                )
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                languages.forEach { locale ->
                    DropdownMenuItem(
                        text = { 
                            Text("${locale.displayLanguage} (${locale.displayCountry})") 
                        },
                        onClick = {
                            onLanguageSelected(locale)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

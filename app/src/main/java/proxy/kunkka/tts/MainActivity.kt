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
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.Locale
import android.widget.Toast
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    private lateinit var textToSpeech: TextToSpeech
    private var availableLanguages = mutableStateOf<List<Locale>>(emptyList())
    private var selectedLanguage = mutableStateOf<Locale?>(null)
    private var speechJob: Job? = null
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
                                }
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
    private fun startSpeaking(onStart: () -> Unit, onError: () -> Unit, onFinish: () -> Unit) {
        speechJob?.cancel()
        speechJob = CoroutineScope(Dispatchers.Default).launch {
            onStart()
            val start = 41111
            val end = 999999
             for (i in start..end) {
                if (isActive) { // Check if coroutine is still active
                    val numberStr = i.toString()
                    val result = textToSpeech.speak(numberStr, TextToSpeech.QUEUE_ADD, null, numberStr)
                    if (result == TextToSpeech.ERROR) {
                        withContext(Dispatchers.Main) { onError() }
                        break
                    }
                    delay(2000) // Adjust delay as needed
                } else {
                    break
                }
            }
            if (isActive) {
                withContext(Dispatchers.Main) { onFinish() }
            }
        }
    }
     private fun stopSpeaking() {
        textToSpeech.stop()
        speechJob?.cancel()
    }
    
    override fun onDestroy() {
         if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        speechJob?.cancel()
        super.onDestroy()
    }


    @Composable
    fun TTSContent(
        modifier: Modifier = Modifier,
        languages: List<Locale>,
        selectedLanguage: Locale?,
        onLanguageSelected: (Locale) -> Unit,
    ) {
        var isSpeaking by remember { mutableStateOf(false) }
        var currentNumber by remember { mutableStateOf("Not started") }

        /// Setup TTS listener
        LaunchedEffect(Unit) {
            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    utteranceId?.let { currentNumber = "Speaking: $it" }
                }

                override fun onDone(utteranceId: String?) {
                    // Optional: Handle completion of each utterance
                }

                override fun onError(utteranceId: String?) {
                    currentNumber = "Error occurred"
                }
            })
        }

        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                    text = currentNumber,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            Button(
                onClick =  {
                    if (!isSpeaking) {
                        startSpeaking(
                            onStart = { isSpeaking = true },
                            onError = { currentNumber = "TTS Error" },
                            onFinish = {
                                currentNumber = "Finished"
                                isSpeaking = false
                            }
                        )
                    }
                },
                enabled = !isSpeaking
            ) {
                
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
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    stopSpeaking()
                    isSpeaking = false
                    currentNumber = "Stopped"
                },
                enabled = isSpeaking
            ) {
                Text("Stop Speaking")
            }
        }
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

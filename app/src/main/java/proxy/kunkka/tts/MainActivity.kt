package proxy.kunkka.tts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import proxy.kunkka.tts.ui.theme.TTSGoTheme
import androidx.compose.material3.MaterialTheme

import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.UpdateFrom
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
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
                    MaterialTheme {
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
        checkUpdate()
        
    }
    fun checkUpdate(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppUpdater(this@MainActivity)
                    //.setUpdateFrom(UpdateFrom.GITHUB)
                    //.setGitHubUserAndRepo("softtiny", "Counting_sheep")
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON("https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json")
                    .start()
            } catch (e: Exception) {
                // Log the error
                //e.printStackTrace()
                // Show a toast message with error information
                //Toast.makeText(this, "Failed to check for updates: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun startSpeaking(start: Int, end: Int, onStart: () -> Unit, onError: () -> Unit, onFinish: () -> Unit) {
        speechJob?.cancel()
        speechJob = CoroutineScope(Dispatchers.Default).launch {
            onStart()
             for (i in start..end) {
                if (isActive) { // Check if coroutine is still active
                    val numberStr = i.toString()
                    val result = textToSpeech.speak(numberStr, TextToSpeech.QUEUE_ADD, null, numberStr)
                    if (result == TextToSpeech.ERROR) {
                        withContext(Dispatchers.Main) { onError() }
                        break
                    }
                    while (textToSpeech.isSpeaking && isActive) {
                        delay(100) // Poll every 100ms
                    }
                    if (isActive) delay(200)
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
        var startRange by remember { mutableStateOf("45678") } // User input for start
        var endRange by remember { mutableStateOf("999999") }   // User input for end
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
                        val start = startRange.toIntOrNull() ?: return@Button
                        val end = endRange.toIntOrNull() ?: return@Button
                        if (start <= end) {
                            startSpeaking(
                                start = start,
                                end = end,
                                onStart = { isSpeaking = true },
                                onError = { currentNumber = "TTS Error" },
                                onFinish = {
                                    currentNumber = "Finished"
                                    isSpeaking = false
                                }
                            )
                        } else {
                            currentNumber="error!"
                        }
                        
                    }
                },
                enabled = !isSpeaking && startRange.isNotEmpty() && endRange.isNotEmpty()
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
             // Start Range Input
            OutlinedTextField(
                value = startRange,
                onValueChange = { startRange = it.filter { char -> char.isDigit() } },
                label = { Text("Start Range") },
                modifier = Modifier.fillMaxWidth(0.8f),
                enabled = !isSpeaking
            )

            Spacer(modifier = Modifier.height(8.dp))

            // End Range Input
            OutlinedTextField(
                value = endRange,
                onValueChange = { endRange = it.filter { char -> char.isDigit() } },
                label = { Text("End Range") },
                modifier = Modifier.fillMaxWidth(0.8f),
                enabled = !isSpeaking
            )
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

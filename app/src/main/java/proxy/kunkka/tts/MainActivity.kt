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

class MainActivity : ComponentActivity() {
    private lateinit var textToSpeech: TextToSpeech
    private var availableLanguages: List<Locale> = emptyList()
    //https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize TTS
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                availableLanguages = textToSpeech.availableLanguages.toList()
                // Force recomposition to update the UI
                setContent {
                    TTSGoTheme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            TTSContent(
                                modifier = Modifier.padding(innerPadding),
                                languages = availableLanguages,
                                onSpeak = { speakNumbers() }
                            )
                        }
                    }
                }
            }
        }

        AppUpdater(this)
            .setUpdateFrom(UpdateFrom.JSON)
            .setUpdateJSON("https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json")
            .start();
    }
}

@Composable
fun TTSContent(
    modifier: Modifier = Modifier,
    languages: List<Locale>,
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
            text = "Available Languages:",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(languages) { locale ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = locale.displayLanguage,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = locale.displayCountry,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TTSGoTheme {
        Greeting("Android")
    }
}
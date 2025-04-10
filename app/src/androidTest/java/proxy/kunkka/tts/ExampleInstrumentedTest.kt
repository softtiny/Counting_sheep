package proxy.kunkka.tts

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import android.util.Log


import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*


import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.github.javiersantos.appupdater.enums.AppUpdaterError

import kotlinx.coroutines.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test 
    fun useJobUpdate() {
        Log.i("useJobUpdate"," run ok use job update")
        Log.i("ExampleInstrumentedTest","run useJobUpdate use context start")
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        CoroutineScope(Dispatchers.IO).launch {
    //     // //     try {
    //     // //         AppUpdater(appContext)
    //     // //             //.setUpdateFrom(UpdateFrom.GITHUB)
    //     // //             //.setGitHubUserAndRepo("softtiny", "Counting_sheep")
    //     // //             .setUpdateFrom(UpdateFrom.JSON)
    //     // //             .setUpdateJSON("https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json")
    //     // //             .start()
    //     // //     } catch (e: Exception) {
    //     // //         Log.i("ExampleInstrumentedTest","run useJobUpdate use context error")
    //     // //         // Log the error
    //     // //         //e.printStackTrace()
    //     // //         // Show a toast message with error information
    //     // //         Toast.makeText(this, "Failed to check for updates: ${e.message}", Toast.LENGTH_LONG).show()
    //     // //     }
            Thread.sleep(20000)
            Log.i("ExampleInstrumentedTest","run useJobUpdate use context end")
        }
    //     // Thread.sleep(30000)
    //     // Log.i("ExampleInstrumentedTest","run useJobUpdate use context end2")
    }
    // @Test 
    // fun useAppUpdate() {
    //     Log.i("ExampleInstrumentedTest","run useAppUpdate use context start")
    //     val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    //     val aa= AppUpdater(appContext)
    //         .setDisplay(Display.NOTIFICATION)
    //         .setUpdateFrom(UpdateFrom.JSON)
    //         .setUpdateJSON("https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json")
    //         .start()
    //     Thread.sleep(20000)
    //     Log.i("ExampleInstrumentedTest","run useAppUpdate use context end")
    // }
    @Test
    fun useAppContext() {
        Log.i("ExampleInstrumentedTest", "run Example Instrumented Test use App context start")
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("proxy.kunkka.tts", appContext.packageName)
        Log.i("ExampleInstrumentedTest","run Example Instrumented Test use App context ok")
        try {
            Log.i("ExampleInstrumentedTest","run AppUpdater App use context")
            // val aa= AppUpdater(appContext)
            //     .setUpdateFrom(UpdateFrom.JSON)
            //     .setUpdateJSON("https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json")
            //     .start()
            val appUpdaterUtils = AppUpdaterUtils(appContext)
                                    .setUpdateFrom(UpdateFrom.JSON)
                                    .setUpdateJSON("https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json")
                                    .withListener(object: AppUpdaterUtils.UpdateListener {
                                        override fun onSuccess(update: Update, isUpdateAvailable: Boolean ) {
                                            Log.d("Latest Version", update.getLatestVersion())
                                            Log.d("Latest Version Code", update.getLatestVersionCode().toString())
                                            Log.d("Release notes", update.getReleaseNotes())
                                            Log.d("URL", update.getUrlToDownload().toString())
                                            Log.d("Is update available?", isUpdateAvailable.toString())
                                        }
                                        override fun onFailed(error: AppUpdaterError) { 
                                            Log.d("AppUpdater Error", "Something went wrong")
                                        }
                                    })
                                    .start()
            Thread.sleep(20000)
            Log.i("ExampleInstrumentedTest","run AppUpdater App use context end")
        } catch (e: Exception) {
            // Log the error
            //e.printStackTrace()
            // Show a toast message with error information
            Log.i("ExampleInstrumentedTest","run AppUpdater App use context fail catch err")
        }
    }
}
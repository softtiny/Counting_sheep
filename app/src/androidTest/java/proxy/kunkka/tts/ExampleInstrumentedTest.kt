package proxy.kunkka.tts

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import android.util.Log


import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        Log.i("ExampleInstrumentedTest", "run Example Instrumented Test use App context start")
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("proxy.kunkka.tts", appContext.packageName)
        Log.i("ExampleInstrumentedTest","run Example Instrumented Test use App context ok")
    }
}
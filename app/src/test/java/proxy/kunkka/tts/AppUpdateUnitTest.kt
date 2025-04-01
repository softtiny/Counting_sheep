package proxy.kunkka.tts

import org.junit.Test

import org.junit.Assert.*
import java.net.URL
import java.net.HttpURLConnection
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader 
import java.nio.charset.Charset

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AppUpdateUnitTest {
    @Test
    fun app_update_is_ok() {
        assertEquals(5, 2 + 3)
    }

}
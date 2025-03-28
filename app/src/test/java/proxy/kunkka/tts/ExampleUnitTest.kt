package proxy.kunkka.tts

import org.junit.Test

import org.junit.Assert.*
import java.net.URL
import java.net.HttpURLConnection
import java.io.InputStream

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(5, 2 + 3)
    }
    @Test
    fun urlreq_isok(){
        var inputStream: InputStream? = null
        val jsonUrl = URL("https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json")
        val connection = jsonUrl.openConnection() as HttpURLConnection
        val statusCode = connection.getResponseCode()
         println("Status Code: $statusCode, HTTP_MOVED_TEMP: ${HttpURLConnection.HTTP_MOVED_TEMP}, HTTP_MOVED_PERM: ${HttpURLConnection.HTTP_MOVED_PERM}, HTTP_SEE_OTHER: ${HttpURLConnection.HTTP_SEE_OTHER}")



        if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    statusCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    statusCode == HttpURLConnection.HTTP_SEE_OTHER ||
                    statusCode == 307 ||
                    statusCode == 308) {
            assertEquals(5, 2 + 3)
        } else {
            assertEquals(5, 3 + 3)
        }
    }
}
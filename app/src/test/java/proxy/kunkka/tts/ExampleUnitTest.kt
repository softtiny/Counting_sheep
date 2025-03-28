package proxy.kunkka.tts

import org.junit.Test

import org.junit.Assert.*
import java.net.URL
import java.net.HttpURLConnection
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamRead
import java.nio.charset.Charset

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
        var inputStream: InputStream!
        var jsonUrl = URL("https://github.com/softtiny/Counting_sheep/releases/latest/download/update-changelog.json")
        var connection = jsonUrl.openConnection() as HttpURLConnection
        connection.setInstanceFollowRedirects(false)
        var statusCode = connection.getResponseCode()
        println("Status Code: $statusCode, HTTP_MOVED_TEMP: ${HttpURLConnection.HTTP_MOVED_TEMP}, HTTP_MOVED_PERM: ${HttpURLConnection.HTTP_MOVED_PERM}, HTTP_SEE_OTHER: ${HttpURLConnection.HTTP_SEE_OTHER}")



        if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    statusCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    statusCode == HttpURLConnection.HTTP_SEE_OTHER ||
                    statusCode == 307 ||
                    statusCode == 308) {
            assertEquals(5, 2 + 3)
            var redirectUrl = connection.getHeaderField("Location")
            jsonUrl= URL(redirectUrl)
            connection = jsonUrl.openConnection() as HttpURLConnection
            connection.setInstanceFollowRedirects(false)
            statusCode = connection.getResponseCode()
            println("Status Code: $statusCode, HTTP_MOVED_TEMP: ${HttpURLConnection.HTTP_MOVED_TEMP}, HTTP_MOVED_PERM: ${HttpURLConnection.HTTP_MOVED_PERM}, HTTP_SEE_OTHER: ${HttpURLConnection.HTTP_SEE_OTHER}")
        } else {
            assertEquals(5, 3 + 3)
        }
        inputStream =  connection.getInputStream()
        var rd =  BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        var sb =  StringBuilder()
        var cp:char!
        while ((cp = rd.read()) != -1) {
            sb.append(cp as char)
        }
        var jsonText = sb.toString()
        println("Response body:$jsonText")

    }
}
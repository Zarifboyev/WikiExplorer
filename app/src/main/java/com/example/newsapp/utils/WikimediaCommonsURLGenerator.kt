import android.util.Log
import com.example.newsapp.presentation.ui.sheets.ModalBottomSheet.Companion.TAG
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object WikimediaCommonsURLGenerator {

    private const val BASE_URL = "https://upload.wikimedia.org/wikipedia/commons"

    fun generateFileURL(fileName: String): String {
        val withoutPrefix = removeFilePrefix(fileName)
        val md5Hash = md5Hash(fileName) ?: return "" // Return empty string if MD5 hash is null
        val hashFirstChar = md5Hash.substring(0, 1)
        val hashFirstTwoChars = md5Hash.substring(0, 2)
        return "$BASE_URL/$hashFirstChar/$hashFirstTwoChars/$withoutPrefix"
    }

    private fun removeFilePrefix(fileName: String): String {
        return if (fileName.startsWith("File:")) {
            fileName.substring("File:".length)
        } else {
            fileName
        }
    }

    private fun md5Hash(input: String): String? {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(input.toByteArray())

            val hexString = StringBuilder()
            for (b in messageDigest) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "MD5 algorithm not found", e)
            null
        }
    }
}

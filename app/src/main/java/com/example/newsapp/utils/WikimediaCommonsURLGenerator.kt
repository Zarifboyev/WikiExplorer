package com.example.newsapp.utils

import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object WikimediaCommonsURLGenerator {
    private const val TAG = "WikimediaCommonsURL"

    fun generateFileURL(fileName: String, thumbnailWidth: Int): String {
        // Base URL
        val baseUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb"

        // Compute MD5 hash of the file name
        val md5Hash = md5Hash(fileName)

        // Get parts of the hash for URL
        val hashFirstTwoChars = md5Hash?.substring(0, 2) ?: "00"

        // Construct the URL using string templates
        return "$baseUrl/$hashFirstTwoChars/$fileName/$thumbnailWidth" +
                "px-$fileName"
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

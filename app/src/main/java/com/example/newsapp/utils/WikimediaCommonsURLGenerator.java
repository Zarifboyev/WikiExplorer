package com.example.newsapp.utils;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WikimediaCommonsURLGenerator {

    private static final String TAG = "WikimediaCommonsURL";

    public static String generateFileURL(String fileName, int thumbnailWidth) {
        // Base URL
        String baseUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb";

        // Compute MD5 hash of the file name
        String md5Hash = md5Hash(fileName);

        // Get parts of the hash for URL
        String hashFirstChar = md5Hash.substring(0, 1);
        String hashFirstTwoChars = md5Hash.substring(0, 2);

        // Construct the URL
        String fileUrl = baseUrl + "/" + hashFirstChar + "/" + hashFirstTwoChars + "/" +
                fileName + "/" + thumbnailWidth + "px-" + fileName;

        return fileUrl;
    }

    private static String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "MD5 algorithm not found", e);
            return null;
        }
    }
}

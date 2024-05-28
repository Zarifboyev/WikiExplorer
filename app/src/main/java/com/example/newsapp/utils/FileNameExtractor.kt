package com.example.newsapp.utils

object FileNameExtractor {
    fun extractFileName(input: String): String {
        // Check if the input starts with "{File:" and ends with "}"
        if (input.startsWith("{File:") && input.endsWith("}")) {
            // Find the index of ':' and '}'
            val colonIndex = input.indexOf(':')
            val braceIndex = input.indexOf('}')
            if (colonIndex != -1 && braceIndex != -1 && colonIndex < braceIndex) {
                // Extract the substring between ":" and "}"
                val fileNameWithExtension = input.substring(colonIndex + 1, braceIndex)
                return fileNameWithExtension.trim { it <= ' ' } // Remove leading and trailing spaces
            }
        }
        // Return an empty string or handle the case as needed if the format is incorrect
        return ""
    }
}

package com.example.newsapp.utils;

public class FileNameExtractor {

    public static String extractFileName(String input) {
        // Check if the input starts with "{File:" and ends with "}"
        if (input.startsWith("{File:") && input.endsWith("}")) {
            // Find the index of ':' and '}'
            int colonIndex = input.indexOf(':');
            int braceIndex = input.indexOf('}');
            if (colonIndex != -1 && braceIndex != -1 && colonIndex < braceIndex) {
                // Extract the substring between ":" and "}"
                String fileNameWithExtension = input.substring(colonIndex + 1, braceIndex);
                return fileNameWithExtension.trim(); // Remove leading and trailing spaces
            }
        }
        // Return an empty string or handle the case as needed if the format is incorrect
        return "";
    }
}

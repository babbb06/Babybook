package com.example.babybook.emoji;

import android.text.InputFilter;
import android.text.Spanned;


public class EmojiInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // Check each character in the input
        for (int i = start; i < end; i++) {
            char character = source.charAt(i);
            // Check if the character is an emoji
            if (Character.getType(character) == Character.SURROGATE) {
                return ""; // Disallow the emoji character
            }
            // Check if the character is a number or allowed special character for email
            if (Character.isDigit(character) || isAllowedEmailCharacter(character)) {
                continue; // Allow the number or special character
            }
            // If the character is not allowed, return an empty string to disallow it
            if (!Character.isLetter(character)) {
                return ""; // Disallow any other character
            }
        }
        return null; // Allow the input
    }

    private boolean isAllowedEmailCharacter(char character) {
        // Define allowed special characters for email
        return character == '@' || character == '.' || character == '-' || character == '_';
    }
}


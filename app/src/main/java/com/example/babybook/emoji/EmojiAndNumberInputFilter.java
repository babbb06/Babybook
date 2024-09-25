package com.example.babybook.emoji;

import android.text.InputFilter;
import android.text.Spanned;

public class EmojiAndNumberInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // Check each character in the input
        for (int i = start; i < end; i++) {
            char character = source.charAt(i);
            // Check if the character is an emoji or a digit or a special character
            if (Character.getType(character) == Character.SURROGATE || Character.isDigit(character) ||
                    !Character.isLetter(character) && !Character.isWhitespace(character) && character != 'ñ' && character != 'Ñ') {
                return ""; // Disallow the character
            }
        }
        return null; // Allow the input
    }
}

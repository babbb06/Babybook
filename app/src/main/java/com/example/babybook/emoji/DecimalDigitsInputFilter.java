package com.example.babybook.emoji;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalDigitsInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        // Concatenate the new input with the existing input
        String result = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend);

        // Define the regex pattern to allow up to 2 digits before the decimal and 3 after
        if (!result.matches("^(\\d{0,2})(\\.\\d{0,3})?$")) {
            return "";
        }

        return null; // Accept the input
    }
}

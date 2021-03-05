package com.amansiol.goonline.tokenizer;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView;

public class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer {
    private int i;

    // Returns the start of the token that ends at offset cursor within text.
    public int findTokenStart(CharSequence inputText, int cursor) {
        int idx = cursor;

        while (idx > 0 && inputText.charAt(idx - 1) != ' ') {
            idx--;
        }
        while (idx < cursor && inputText.charAt(idx) == ' ') {
            idx++;
        }
        return idx;
    }

    // Returns the end of the token (minus trailing punctuation) that
    // begins at offset cursor within text.
    public int findTokenEnd(CharSequence inputText, int cursor) {
        int idx = cursor;
        int length = inputText.length();

        while (idx < length) {
            if (inputText.charAt(i) == ' ') {
                return idx;
            } else {
                idx++;
            }
        }
        return length;
    }

    // Returns text, modified, if necessary, to ensure that it ends with a token terminator
    // (for example a space or comma).
    public CharSequence terminateToken(CharSequence inputText) {
        int idx = inputText.length();

        while (idx > 0 && inputText.charAt(idx - 1) == ' ') {
            idx--;
        }

        if (idx > 0 && inputText.charAt(idx - 1) == ' ') {
            return inputText;
        } else {
            if (inputText instanceof Spanned) {
                SpannableString sp = new SpannableString(inputText + " ");
                TextUtils.copySpansFrom((Spanned) inputText, 0, inputText.length(),
                        Object.class, sp, 0);
                return sp;
            } else {
                return inputText + " ";
            }
        }
    }
}

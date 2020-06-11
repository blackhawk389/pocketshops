package com.appabilities.sold.utils;

import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;

/**
 * Created by Wajahat on 8/26/2016.
 */

public class TextValidatiors {

    public static boolean isEmpty(@Nullable CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    public static boolean isEmpty(EditText editText, String message) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError(message);
            editText.requestFocus();
            return true;
        } else return false;
    }

    public static boolean isEmpty(String message, EditText... editTexts ) {

        for(EditText whatever : editTexts){
            // do what ever you want
            if (TextUtils.isEmpty(whatever.getText().toString())) {
                whatever.setError(message);
                whatever.requestFocus();
                return true;
            }
        }

        return false;
    }

    public static boolean isGreatThan(EditText editText, String message, String minValue, String currentValue)
    {
        float min = Float.parseFloat(minValue);
        float current = Float.parseFloat(currentValue);
        if (current < min)
        {
            editText.setError(message);
            editText.requestFocus();
            return true;
        } else return false;
    }

    public static boolean isValidEmail(CharSequence target) {

        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isPasswordMatched(EditText password, EditText confirmPassword, String emptyMessage, String message) {
        // If any one is empty
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(emptyMessage);
            password.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword.getText().toString())) {
            confirmPassword.setError(emptyMessage);
            confirmPassword.requestFocus();
            return false;
        }

        if (!password.getText().toString().equals(confirmPassword.getText().toString()))
        {
            confirmPassword.setError(message);
            confirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    public static boolean hasMinimunLength(EditText editText, int length, String message)
    {
        if (editText.getText().toString().length() < length)
        {
            editText.setError(message);
            return false;
        }
        else return true;
    }

}

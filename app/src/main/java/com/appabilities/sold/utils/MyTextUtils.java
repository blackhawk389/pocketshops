package com.appabilities.sold.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

/**
 * Created by Wajahat on 5/3/2017.
 */

public class MyTextUtils {

    public static CharSequence getOwnerName(String owner)
    {
        owner = "Listed by " + owner;
        Spannable wordtoSpan = new SpannableString(owner);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#881b42")), owner.indexOf("by ") + 3, owner.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), owner.indexOf("by ") + 3, owner.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return wordtoSpan;
    }
}

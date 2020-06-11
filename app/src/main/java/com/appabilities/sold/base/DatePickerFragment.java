package com.appabilities.sold.base;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.appabilities.sold.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Wajahat on 9/9/2016.
 */

public class DatePickerFragment extends DialogFragment {

    public Context context;
    public EditText txtDate;
    public boolean onlyFuture = true;

    public DatePickerFragment() {
    }

    public void setParams(Context context, EditText txtDate) {
        this.context = context;
        this.txtDate = txtDate;
        onlyFuture = true;
    }

    public void setParams(Context context, EditText txtDate, boolean onlyFuture) {
        this.context = context;
        this.txtDate = txtDate;
        this.onlyFuture = onlyFuture;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(context, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                txtDate.setText(year + "-" + String.format("%02d", (month+1)) + "-" + String.format("%02d", (day)));
                //dateSet = true;
            }
        }, year, month, day);

        if (onlyFuture) dialog.getDatePicker().setMinDate(new Date().getTime()  - 10000);

        // Create a new instance of DatePickerDialog and return it
        //return new DatePickerDialog(getActivity(), , year, month, day);
        return dialog;
    }
}

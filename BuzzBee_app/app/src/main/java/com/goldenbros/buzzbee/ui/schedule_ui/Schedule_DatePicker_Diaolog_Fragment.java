package com.goldenbros.buzzbee.ui.schedule_ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;

import java.util.Calendar;

/**
 * Created by wang on 7/28/15.
 */
public class Schedule_DatePicker_Diaolog_Fragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    static final int DATE_DIALOG_ID = 999;
    private StringBuilder dateSb;
    private int year;
    private int month;
    private int day;

    public interface EventDatePickerListener
    {
        void onEventDatePickComplete(String date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get current date
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        dateSb = new StringBuilder();

        return new DatePickerDialog(getActivity(), this, year, month ,day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        populateSetDate(year, monthOfYear + 1, dayOfMonth);
    }

    private void populateSetDate(int year, int month, int day) {
        // set selected date into dateTextView
        dateSb.append(year).append("-").append(month).append("-").append(day);

        Toast.makeText(getActivity(),
                "You select: " + dateSb.toString(), Toast.LENGTH_LONG).show();

        this.dismiss();
        EventDatePickerListener listener = (EventDatePickerListener) getActivity().getSupportFragmentManager().findFragmentById(R.id.container_body);
        listener.onEventDatePickComplete(dateSb.toString());
    }

}

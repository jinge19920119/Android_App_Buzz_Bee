package com.goldenbros.buzzbee.ui.event_ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;

/**
 * Created by wang on 7/27/15.
 */
public class Event_Issue_TimeDialog_Fragment extends DialogFragment
        implements AdapterView.OnItemSelectedListener {
    private Spinner timeSpinner;
    private String time;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView myText = (TextView) view;
        Toast.makeText(getActivity(), "You Selected " + myText.getText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public interface TimeChooseListener {
        void onTimeChooseComplete(String time);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.event_issue_time_choose_fragment, null);

        //get time
        timeSpinner = (Spinner) view.findViewById(R.id.event_issue_time_spinner);
        ArrayAdapter timeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.period_array,
                android.R.layout.simple_spinner_item);
        timeSpinner.setAdapter(timeAdapter);
        timeSpinner.setOnItemSelectedListener(this);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                time = timeSpinner.getSelectedItem().toString();
                                Log.d("......time:", time);
                                TimeChooseListener listener = (TimeChooseListener) getActivity();
                                listener.onTimeChooseComplete(time);
                            }
                        }).setNegativeButton("Cancel", null);
        return builder.create();
    }
}

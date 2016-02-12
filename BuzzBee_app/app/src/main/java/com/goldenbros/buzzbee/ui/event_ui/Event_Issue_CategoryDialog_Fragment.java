package com.goldenbros.buzzbee.ui.event_ui;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
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
 * Created by wang on 7/26/15.
 */
public class Event_Issue_CategoryDialog_Fragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private Spinner categorySpinner;
    private String category;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView myText = (TextView) view;
        Toast.makeText(getActivity(), "You Selected " + myText.getText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface CategoryChooseListener {
        void onCategoryChooseComplete(String category);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.event_category_choose_fragment, null);

        //get category
        categorySpinner = (Spinner) view.findViewById(R.id.event_issue_category_spinner);
        ArrayAdapter categoryAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.category_array,
                android.R.layout.simple_spinner_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(this);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
            .setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            category = categorySpinner.getSelectedItem().toString();
                            Log.d("......category:", category);
                            CategoryChooseListener listener = (CategoryChooseListener) getActivity();
                            listener.onCategoryChooseComplete(category);
                        }
                    }).setNegativeButton("Cancel", null);
        return builder.create();
    }
}

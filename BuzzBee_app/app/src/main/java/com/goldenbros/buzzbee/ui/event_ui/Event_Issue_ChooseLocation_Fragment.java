package com.goldenbros.buzzbee.ui.event_ui;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

/**
 * Created by wang on 7/28/15.
 */
public class Event_Issue_ChooseLocation_Fragment extends DialogFragment {

    private final LatLng OriginalPoint = new LatLng(40.44279950938586 , -79.94348745793104);
    private GoogleMap googleMap;
    private Marker marker;
    private static View view;

    private LatLng selectedLocation;

    public interface LocationChooseListener {
        void onLocationChooseComplete(LatLng selectedLocation);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        if(view == null)
            view = getActivity().getLayoutInflater().inflate(R.layout.event_issue_map_dialog_fragment, null);

        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getActivity().getFragmentManager().
                        findFragmentById(R.id.event_issue_map)).getMap();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setMyLocationEnabled(true);

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(OriginalPoint, 13);
            googleMap.animateCamera(update);

            marker = googleMap.addMarker(new MarkerOptions()
                    .position(OriginalPoint).title("Original Location"));

            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng point) {
                    if (marker != null) {
                        marker.remove();
                    }
                    Toast.makeText(getActivity().getBaseContext(),
                            point.toString(), LENGTH_SHORT).show();

                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(point).title("New Place"));

                    Log.d("La:" + point.latitude, "Lo" + point.longitude);
                    selectedLocation = point;
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                LocationChooseListener listener = (LocationChooseListener) getActivity();
                                listener.onLocationChooseComplete(selectedLocation);
                            }
                        }).setNegativeButton("Cancel", null);
        return builder.create();
    }
}

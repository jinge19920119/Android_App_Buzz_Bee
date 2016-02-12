package com.goldenbros.buzzbee.ui.event_ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.aws.AWSConnector;
import com.goldenbros.buzzbee.client.DefaultSocketClient_ClientSide;
import com.goldenbros.buzzbee.model.Event;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.ui.login_ui.Login_Activity;
import com.goldenbros.buzzbee.util.CommandConstants;
import com.goldenbros.buzzbee.util.ConfirmMsgConstants;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Event_Issue_Activity extends FragmentActivity
        implements View.OnClickListener,
        Event_Issue_CategoryDialog_Fragment.CategoryChooseListener,
        Event_Issue_TimeDialog_Fragment.TimeChooseListener,
        Event_Issue_ChooseImage_Fragment.ChooseEventImageListener,
        Event_Issue_ChooseLocation_Fragment.LocationChooseListener {

    private static final String LOCAL_HOST = "128.237.167.57";
    private static final int PORT = 4444;

    private static final int TAKE_PICTURE = 1;
    private static final int DIALOG_FRAGMENT = 2;
    private static final int PHOTO_SELECTED = 3;

    public static final int MEDIA_TYPE_IMAGE = 10;
    private Uri fileUri; // file url to store image
    private static final String IMAGE_DIRECTORY_NAME = "BuzzBeeEventCamera";

    private AWSConnector awsConnector;

    private final String LOG_TAG = this.getClass().getSimpleName();

    private EditText eventNameEditText;

    private TextView categoryTextView;
    private ImageButton categoryChooseButton;

    private TextView dateTextView;
    private ImageButton dateChooseButton;

    static final int DATE_DIALOG_ID = 999;
    private StringBuilder dateSb;
    private int year;
    private int month;
    private int day;

    private TextView timeTextView;
    private ImageButton timeChooseButton;

    private ImageView eventImageView;
    private StringBuilder eventImageUriSb;
    private String eventImageUri;

    private EditText descriptionEditText;

    private TextView locationTextView;
    private ImageButton locationChooseButton;
    private String eventLocation;

    private EditText populationEditText;

    private Button createActivityButton;

    private String currUserEmail;
    private String currUserPassword;

    private StringBuilder sb;

    private int e_upload_id;

    private static final String AWS_STORE_PATH = "https://s3.amazonaws.com/buzzbeestorage-user/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_issue_activity);

        eventNameEditText = (EditText) findViewById(R.id.eventNameEditText);

        categoryTextView = (TextView) findViewById(R.id.category_choose_TextView);
        categoryChooseButton = (ImageButton) findViewById(R.id.category_choose_button);
        categoryChooseButton.setOnClickListener(this);

        dateTextView = (TextView) findViewById(R.id.date_choose_TextView);
        dateChooseButton = (ImageButton) findViewById(R.id.date_choose_button);
        dateChooseButton.setOnClickListener(this);

        timeTextView = (TextView) findViewById(R.id.time_choose_TextView);
        timeChooseButton = (ImageButton) findViewById(R.id.time_choose_button);
        timeChooseButton.setOnClickListener(this);

        eventImageView = (ImageView) findViewById(R.id.eventImageView);
        eventImageView.setClickable(true);
        eventImageView.setOnClickListener(this);

        //aws s3 connector
        Random rand = new Random();
        e_upload_id = rand.nextInt(7000) + 500;
        awsConnector = new AWSConnector(Event_Issue_Activity.this, eventImageView, e_upload_id);

        descriptionEditText = (EditText) findViewById(R.id.event_description_EditText);

        locationTextView = (TextView) findViewById(R.id.location_choose__TextView);
        locationChooseButton = (ImageButton) findViewById(R.id.location_choose_button);
        locationChooseButton.setOnClickListener(this);

        populationEditText = (EditText) findViewById(R.id.populationEditText);

        createActivityButton = (Button) findViewById(R.id.createActivityButton);
        createActivityButton.setOnClickListener(this);


        Bundle extra = getIntent().getExtras();
        currUserEmail = extra.getString(Login_Activity.EMAIL);
        Log.d("~~~~~~Ev_Is_Ac", "email:" + currUserEmail);
        currUserPassword = extra.getString(Login_Activity.PASSWORD);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case  R.id.category_choose_button:
                Log.v(LOG_TAG, "Click category choose button...");
                //choose category
                showCategoryDiaglog();
                break;

            case R.id.date_choose_button:
                //choose date
                Log.v(LOG_TAG, "Click date choose button...");
                showDateDialog();
                break;

            case R.id.time_choose_button:
                //choose time
                Log.v(LOG_TAG, "Click time choose button...");
                showTimeDialog();
                break;

            case R.id.eventImageView:
                //start camera to take a picture or choose from photo booklets
                Log.v(LOG_TAG, "Click image choose button...");
                showChooseImageFragment();
                //set image to imageView
                break;

            case R.id.location_choose_button:
                //start google map to find location
                //set location information to locationTextView;
                showChooseLocationFragement();
                break;

            //create activity
            case R.id.createActivityButton:
                //get event name
                String name = eventNameEditText.getText().toString();
                if(name.equals("")) {
                    Toast.makeText(this.getBaseContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //get category
                String category = categoryTextView.getText().toString();
                if(category.equals("Tap to Select")) {
                    Toast.makeText(this.getBaseContext(), "Category can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //get date
                String date = dateTextView.getText().toString();
                if(date.equals("Tap to Select")) {
                    Toast.makeText(this.getBaseContext(), "Date can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //get time
                //check time exception
                String time = timeTextView.getText().toString();
                if(time.equals("Tap to Select") || time.equals("Non-specified")) {
                    Toast.makeText(this.getBaseContext(), "Time can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //get image uri
                if(eventImageUri == null) {
                    Toast.makeText(this.getBaseContext(), "Image can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //get description
                String description = descriptionEditText.getText().toString();
                if(description.equals("")) {
                    Toast.makeText(this.getBaseContext(), "Description can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //get location
                String eventLocation = locationTextView.getText().toString();
                if(eventLocation.equals("Tap to Select")) {
                    Toast.makeText(this.getBaseContext(), "Location can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                //get population
                if(Integer.parseInt(populationEditText.getText().toString()) < 2) {
                    Toast.makeText(this.getBaseContext(), "Population can't be only one", Toast.LENGTH_SHORT).show();
                    return;
                }
                int population = Integer.parseInt(populationEditText.getText().toString());

                //Query user info from DB
                sb = new StringBuilder();
                User user = new User();
                user.setEmail(currUserEmail);
                DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                        CommandConstants.QUERY_USER_INFO, user, sb);
                Thread t1 = new Thread(d);
                t1.start();
                while(t1.isAlive()) {

                }
                User queryUser = d.getQueryUser();
                Log.d("-----Query user id", queryUser.getID() + "");
                //get holder_id
                int holder_id = queryUser.getID();

                //1 stands for start, 2 stands for end
                int event_status = 1;

                Event newEvent = new Event(event_status, holder_id, population,
                        name, date, time, eventLocation, category, description, eventImageUri);
                updateEventDB(newEvent);

                break;
        }
    }


    public void showCategoryDiaglog() {
        Event_Issue_CategoryDialog_Fragment dialog = new Event_Issue_CategoryDialog_Fragment();
        dialog.show(getSupportFragmentManager(), "categoryDialog");
    }

    //set choosen category to categoryTextView
    @Override
    public void onCategoryChooseComplete(String category) {
        categoryTextView.setText(category);
    }



    public void showDateDialog() {
        dateSb = new StringBuilder();
        //get current date
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        showDialog(DATE_DIALOG_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            if(selectedYear < year) {
                Toast.makeText(Event_Issue_Activity.this,
                        "Select year error..", Toast.LENGTH_LONG).show();
                return;
            }
            year = selectedYear;

            if(selectedMonth + 1 < month + 1) {
                Toast.makeText(Event_Issue_Activity.this,
                        "Select month error..", Toast.LENGTH_LONG).show();
                return;
            }
            month = selectedMonth + 1;

            if(selectedMonth + 1 == month + 1) {
                if(selectedDay < day) {
                    Toast.makeText(Event_Issue_Activity.this,
                            "Select day error..", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            day = selectedDay;

            // set selected date into dateTextView
            dateSb.append(year).append("-").append(month).append("-").append(day);
            dateTextView.setText(dateSb.toString());

            Toast.makeText(Event_Issue_Activity.this,
                    "You select: " + dateSb.toString(), Toast.LENGTH_LONG).show();
        }
    };



    public void showTimeDialog() {
        Event_Issue_TimeDialog_Fragment dialog = new Event_Issue_TimeDialog_Fragment();
        dialog.show(getSupportFragmentManager(), "timeDialog");
    }

    @Override
    public void onTimeChooseComplete(String time) {
        timeTextView.setText(time);
    }




    public void showChooseImageFragment() {
        Event_Issue_ChooseImage_Fragment dialog = new Event_Issue_ChooseImage_Fragment();
        dialog.show(getSupportFragmentManager(), "chooseEventImageDialog");
    }

    @Override
    public void onStartCameraComplete() {
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(Event_Issue_Activity.this,
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
//            getActivity().finish();
            return;
        }
        takePicture();
    }
    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (Event_Issue_Activity.this.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    /**
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, TAKE_PICTURE);
    }
    /**
     * Creating file uri to store image
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /**
     * returning image
     */
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    public void onSelectPhotoComplete() {
        selectPhoto();
    }
    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_SELECTED);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TAKE_PICTURE:
                if(resultCode == Activity.RESULT_OK) {

                    try {
                        // successfully captured the image
                        // display it in image view
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // downsizing image as it throws OutOfMemory Exception for larger
                        // images
                        options.inSampleSize = 8;
                        final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                                options);
                        eventImageView.setImageBitmap(bitmap);

                        //upload to amazon S3
                        awsConnector.new S3PutObjectTask2(Event_Issue_Activity.this).execute(fileUri.getPath());

                        //download
                        AWSConnector.S3TaskResult result = awsConnector.new S3GeneratePresignedUrlTask().execute().get();
                        String uri = result.getUri().toString();

                        //get event image Uri
                        eventImageUri = uri;

                        String[] wholeUri = eventImageUri.split("\\?");
                        String[] partUri = wholeUri[0].split("/");
                        String storeUri = partUri[3];

                        eventImageUriSb = new StringBuilder();
                        eventImageUriSb.append(AWS_STORE_PATH).append(storeUri);
                        eventImageUri = eventImageUriSb.toString();

                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
                break;

            case PHOTO_SELECTED:
                if (resultCode == Activity.RESULT_OK) {
                    Log.v("check", "Result OK");
                    Uri selectedImage = data.getData();
                    awsConnector.new S3PutObjectTask(Event_Issue_Activity.this).execute(selectedImage);

                    try {
                        AWSConnector.S3TaskResult result = awsConnector.new S3GeneratePresignedUrlTask().execute().get();
                        String uri = result.getUri().toString();

                        //get event image Uri
                        eventImageUri = uri;

                        String[] wholeUri = eventImageUri.split("\\?");
                        String[] partUri = wholeUri[0].split("/");
                        String storeUri = partUri[3];

                        eventImageUriSb = new StringBuilder();
                        eventImageUriSb.append(AWS_STORE_PATH).append(storeUri);
                        eventImageUri = eventImageUriSb.toString();

                        awsConnector.new GetXMLTask().execute(eventImageUri);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.v("check","Result Not OK");
                }
                break;
        }
    }


    public void updateEventDB(Event newEvent) {
        sb = new StringBuilder();

        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.CREATE_NEW_EVENT, newEvent, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("----Acc event info:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.NEW_EVENT_ADDED)) {
            Toast.makeText(Event_Issue_Activity.this, "Create New Event successfully!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Event_Issue_Activity.this, "Create New Event fail...!",
                    Toast.LENGTH_SHORT).show();
        }
    }



    private void showChooseLocationFragement() {
        Event_Issue_ChooseLocation_Fragment dialog = new Event_Issue_ChooseLocation_Fragment();
        dialog.show(getSupportFragmentManager(), "locationDialog");
    }


    @Override
    public void onLocationChooseComplete(LatLng selectedLocation) {
        double lati = selectedLocation.latitude;
        double longi = selectedLocation.longitude;

        eventLocation = lati + "/" + longi;
        locationTextView.setText(eventLocation);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event__issue_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

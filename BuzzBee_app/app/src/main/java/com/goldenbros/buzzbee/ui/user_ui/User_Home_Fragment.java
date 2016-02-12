package com.goldenbros.buzzbee.ui.user_ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.aws.AWSConnector;
import com.goldenbros.buzzbee.client.DefaultSocketClient_ClientSide;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.ui.login_ui.Login_Activity;
import com.goldenbros.buzzbee.util.CommandConstants;
import com.goldenbros.buzzbee.util.ConfirmMsgConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class User_Home_Fragment extends Fragment
        implements View.OnClickListener, User_ChooseImage_Fragment.ChooseImageListener {
    private static final String LOCAL_HOST = "128.237.167.57";
    private static final int PORT = 4444;

    private static final int TAKE_PICTURE = 1;
    private static final int DIALOG_FRAGMENT = 2;
    private static final int PHOTO_SELECTED = 3;

    public static final int MEDIA_TYPE_IMAGE = 10;
    private Uri fileUri; // file url to store image
    private static final String IMAGE_DIRECTORY_NAME = "BuzzBeeCamera";

    private AWSConnector awsConnector;

    private TextView emailView;
    private TextView idView;

    private EditText nameEditView;
    private EditText ageEditView;
    private EditText descriptionEditView;

    private RadioGroup radioSexGroup;
    private RadioButton maleButton;
    private RadioButton femaleButton;
    private RadioButton radioSexButton;

    private Button saveButton;
    private ImageView userProfileImageView;

    private StringBuilder sb;

    //for user audio description
    private Button startRecordButton;
    private Button stopRecordButton;
    private Button playButton;
    private Button stopPlayButton;

    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private String AUDIO_OUTPUT_FILE;

    private Uri audioFileUri; // file url to store audio
    private static final int TYPE_AUDIO = 1;
    private static final String AUDIO_DIRECTORY_NAME = "bb_audio";

    private static final String AWS_STORE_PATH = "https://s3.amazonaws.com/buzzbeestorage-user/";

    public static User_Home_Fragment newInstance(String param1, String param2) {
        return new User_Home_Fragment();
    }

    public User_Home_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_home_fragment, container, false);

        //User profile
        userProfileImageView = (ImageView) view.findViewById(R.id.userImageView);
        userProfileImageView.setClickable(true);
        userProfileImageView.setOnClickListener(this);



        //get user login email
        emailView = (TextView) view.findViewById(R.id.emailTextView);
        Bundle extra = getActivity().getIntent().getExtras();
        String email = extra.getString(Login_Activity.EMAIL);
        emailView.setText(email);
        Log.d("........QueryUser email", email);
        
        idView = (TextView) view.findViewById(R.id.idTextView);
        
        nameEditView = (EditText) view.findViewById(R.id.userNameEditText);
        ageEditView = (EditText) view.findViewById(R.id.userAgeEditText);
        descriptionEditView = (EditText) view.findViewById(R.id.userDescriptionEditText);

        radioSexGroup = (RadioGroup) view.findViewById(R.id.radioSexGroup);
        maleButton = (RadioButton) view.findViewById(R.id.radioMale);
        femaleButton = (RadioButton) view.findViewById(R.id.radioFemale);

        //Query user info from DB
        sb = new StringBuilder();
        User user = new User();
        user.setEmail(email);

        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_USER_INFO, user, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        User queryUser = d.getQueryUser();
        Log.d("-----Query user id", queryUser.getID() + "");

        //supplement user info in User_Home_Page
        idView.setText(queryUser.getID() + "");

        //aws s3 connector
        awsConnector = new AWSConnector(getActivity(), userProfileImageView, queryUser.getID() * 1000);

        if(queryUser.getName() != null) {
            nameEditView.setText(queryUser.getName());
        }
        if(queryUser.getAge() != 0) {
            ageEditView.setText(queryUser.getAge() + "");
        }
        if(queryUser.getSex() != null) {
            if(queryUser.getSex().equals("Male")) {
                maleButton.setChecked(true);
            } else {
                femaleButton.setChecked(true);
            }
        }
        if(queryUser.getDescrip() != null) {
            descriptionEditView.setText(queryUser.getDescrip());
        }
        if(queryUser.getPhotoName() != null) {
            Log.d("~~~~~~~getImage", queryUser.getPhotoName());
            awsConnector.new GetXMLTask().execute(queryUser.getPhotoName());
        }
        if(queryUser.getAudioPath() != null) {
            AUDIO_OUTPUT_FILE = queryUser.getAudioPath();
        }

        //when click "save" button, save all changes into DB
        saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);


        startRecordButton = (Button) view.findViewById(R.id.start_record_button);
        startRecordButton.setOnClickListener(this);

        stopRecordButton = (Button) view.findViewById(R.id.stop_record_button);
        stopRecordButton.setOnClickListener(this);

        playButton = (Button) view.findViewById(R.id.play_button);
        playButton.setOnClickListener(this);

        stopPlayButton = (Button) view.findViewById(R.id.stop_play_button);
        stopPlayButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.saveButton:
                int id = Integer.parseInt(idView.getText().toString());
                String email = emailView.getText().toString();

                String name = nameEditView.getText().toString();
                if(name.equals("")) {
                    Toast.makeText(getActivity().getBaseContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(ageEditView.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getBaseContext(), "Age can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                int age = Integer.parseInt(ageEditView.getText().toString());

                String description = descriptionEditView.getText().toString();
                if(description.equals("")) {
                    Toast.makeText(getActivity().getBaseContext(), "Description can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // get selected radio button from radioGroup
                String sex = "";
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                // if no choose, id == -1
                if(selectedId == maleButton.getId()) {
                    sex = maleButton.getText().toString();
                } else if(selectedId == femaleButton.getId()) {
                    sex = femaleButton.getText().toString();
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "Sex can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(AUDIO_OUTPUT_FILE == null) {
                    Toast.makeText(getActivity().getBaseContext(), "Audio can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                //store audio
                String audioPath = null;
                try {
                    //upload to amazon S3
                    awsConnector.new S3PutObjectTask2(getActivity().getBaseContext()).execute(audioFileUri.getPath());
                    //download
                    AWSConnector.S3TaskResult result = awsConnector.new S3GeneratePresignedUrlTask().execute().get();
                    audioPath = result.getUri().toString();
                } catch(Exception e) {
                    e.printStackTrace();
                }


                //update user image in userDB
                User updateUser = new User(id, email, name, sex, age, description, audioPath);
                updateUserDB(updateUser);
                break;

            case R.id.userImageView:
                showChooseImageFragment();
                break;


            //for audio recording
            case R.id.start_record_button:
                try {
                    beginRecording();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stop_record_button:
                try {
                    stopRecording();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.play_button:
                try {
                    playRecording();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stop_play_button:
                try {
                    stopPlayback();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //update user information in DB
    public void updateUserDB(User updateUser) {
        sb = new StringBuilder();

        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.UPDATE_USER, updateUser, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("----Acc update info:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.USER_UPDATED)) {
            Toast.makeText(getActivity(), "Update User successfully!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Update User fail...!",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void showChooseImageFragment() {
        User_ChooseImage_Fragment dialog = new User_ChooseImage_Fragment();
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        dialog.show(getActivity().getSupportFragmentManager(), "chooseImageDialog");

    }

    @Override
    public void onStartCameraComplete() {
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getActivity().getBaseContext(),
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
        if (getActivity().getBaseContext().getPackageManager().hasSystemFeature(
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
                        userProfileImageView.setImageBitmap(getRoundedShape(bitmap));

                        //upload to amazon S3
                        awsConnector.new S3PutObjectTask2(getActivity().getBaseContext()).execute(fileUri.getPath());

                        //download
                        AWSConnector.S3TaskResult result = awsConnector.new S3GeneratePresignedUrlTask().execute().get();
                        String uri = result.getUri().toString();

//                        String[] wholeUri = uri.split("\\?");
//                        String[] partUri = wholeUri[0].split("/");
//                        String storeUri = partUri[3];
//
//                        storeUri = AWS_STORE_PATH + storeUri;

                        //update user image in userDB
                        User user = new User();
                        user.setID(Integer.parseInt(idView.getText().toString()));
                        user.setEmail(emailView.getText().toString());
                        user.setPhotoName(uri);
                        updateUserDBImage(user);

                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
                break;

            case PHOTO_SELECTED:
                if (resultCode == Activity.RESULT_OK) {
                    Log.v("check", "Result OK");
                    Uri selectedImage = data.getData();
                    awsConnector.new S3PutObjectTask(getActivity().getBaseContext()).execute(selectedImage);

                    try {
                        AWSConnector.S3TaskResult result = awsConnector.new S3GeneratePresignedUrlTask().execute().get();
                        String uri = result.getUri().toString();

//                        String[] wholeUri = uri.split("\\?");
//                        String[] partUri = wholeUri[0].split("/");
//                        String storeUri = partUri[3];
//
//                        storeUri = AWS_STORE_PATH + storeUri;

                        User user = new User();
                        user.setID(Integer.parseInt(idView.getText().toString()));
                        user.setEmail(emailView.getText().toString());
                        user.setPhotoName(uri);
                        updateUserDBImage(user);

                        awsConnector.new GetXMLTask().execute(uri);

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

    /*
     * make circle image
     */
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 200;
        int targetHeight = 200;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(100, 100,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public void updateUserDBImage(User updateUser) {
        sb = new StringBuilder();

        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.UPDATE_USER_IMAGE, updateUser, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("----Acc updateImg info:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.USER_IMAGE_UPDATED)) {
            Toast.makeText(getActivity(), "Update UserImg successfully!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Update UserImg fail...!",
                    Toast.LENGTH_SHORT).show();
        }
    }






    private void beginRecording() throws Exception{
        dispatchMediaRecord();

        audioFileUri = getOutputAudioFileUri(TYPE_AUDIO);
        AUDIO_OUTPUT_FILE = audioFileUri.getPath();

        Log.d("...........", audioFileUri.toString());
        Log.d(",,,,,,,,,,,", AUDIO_OUTPUT_FILE);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(AUDIO_OUTPUT_FILE);
        recorder.prepare();
        recorder.start();

    }

    private void dispatchMediaRecord() {
        if(recorder != null) {
            recorder.release();
        }
    }

    /**
     * Creating file uri to store image
     */
    public Uri getOutputAudioFileUri(int type) {
        return Uri.fromFile(getOutputAudioFile(type));
    }

    /**
     * returning image
     */
    private static File getOutputAudioFile(int type) {
        // External sdcard location
        File audioStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                AUDIO_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!audioStorageDir.exists()) {
            if (!audioStorageDir.mkdirs()) {
                Log.d(AUDIO_DIRECTORY_NAME, "Oops! Failed create "
                        + AUDIO_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        File audioFile;
        if (type == TYPE_AUDIO) {
            audioFile = new File(audioStorageDir.getPath() + File.separator
                    + "AUDIO_" + timeStamp + ".3gpp");
        } else {
            return null;
        }

        return audioFile;
    }

    private void stopRecording() {
        if(recorder != null) {
            recorder.stop();
        }
    }

    private void playRecording() throws Exception{
        dispatchMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(AUDIO_OUTPUT_FILE);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    private void dispatchMediaPlayer() {
        if(mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopPlayback() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }



}

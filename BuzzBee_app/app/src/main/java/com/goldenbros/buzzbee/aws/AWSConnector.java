package com.goldenbros.buzzbee.aws;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.goldenbros.buzzbee.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by wang on 7/27/15.
 */
public class AWSConnector {
    private int owner_id;

    private AmazonS3Client s3Client = new AmazonS3Client(
            new BasicAWSCredentials(com.goldenbros.buzzbee.aws.Constants.ACCESS_KEY_ID,
                    Constants.SECRET_KEY));

    private Activity activity;
    private ImageView imageView;

    public AWSConnector(Activity activity) {
        this.activity = activity;
    }

    public AWSConnector(Activity activity, ImageView imageView, int owner_id) {
        this.activity = activity;
        this.imageView = imageView;
        this.owner_id = owner_id;
    }

    // Display an Alert message for an error or failure.
    public void displayAlert(String title, String message) {

        AlertDialog.Builder confirm = new AlertDialog.Builder(activity);
        confirm.setTitle(title);
        confirm.setMessage(message);

        confirm.setNegativeButton(
                activity.getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

        confirm.show().show();
    }

    public void displayErrorAlert(String title, String message) {

        AlertDialog.Builder confirm = new AlertDialog.Builder(activity);
        confirm.setTitle(title);
        confirm.setMessage(message);

        confirm.setNegativeButton(
                activity.getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                });

        confirm.show().show();
    }


    public class S3PutObjectTask extends AsyncTask<Uri, Void, S3TaskResult> {

        ProgressDialog dialog;
        Context context;

        public S3PutObjectTask(Context context){
            this.context = context;
        }

        protected void onPreExecute() {
            Log.v("check", "S3putObjectTask preExecute");
            dialog = new ProgressDialog(activity);
            dialog.setMessage(activity
                    .getString(R.string.uploading));
            dialog.setCancelable(false);
            dialog.show();
        }

        protected S3TaskResult doInBackground(Uri... uris) {

            if (uris == null || uris.length != 1) {
                return null;
            }

            // The file location of the image selected.
            Uri selectedImage = uris[0];

            String filePath = RealPathUtil.getRealPathFromURI_API19(context, selectedImage);

            S3TaskResult result = new S3TaskResult();
            // Put the image data into S3.
            try {
                s3Client.createBucket(Constants.getPictureBucket());
                // Content type is determined by file extension.
                PutObjectRequest por = new PutObjectRequest(
                        Constants.getPictureBucket(), Constants.PICTURE_NAME + owner_id,
                        new java.io.File(filePath));
                Log.v("check", "S3putObjectTask doInBackground4");
                s3Client.putObject(por);
            } catch (Exception exception) {

                result.setErrorMessage(exception.getMessage());
            }

            return result;
        }

        protected void onPostExecute(S3TaskResult result) {

            dialog.dismiss();

            if (result.getErrorMessage() != null) {

                displayErrorAlert(
                        activity
                                .getString(R.string.upload_failure_title),
                        result.getErrorMessage());
            }
        }

    }


    public class S3PutObjectTask2 extends AsyncTask<String, Void, S3TaskResult> {

        ProgressDialog dialog;
        Context context;

        public S3PutObjectTask2(Context context){
            this.context = context;
        }

        protected void onPreExecute() {
            Log.v("check","S3putObjectTask preExecute");
            dialog = new ProgressDialog(activity);
            dialog.setMessage(activity
                    .getString(R.string.uploading));
            dialog.setCancelable(false);
            dialog.show();
        }

        protected S3TaskResult doInBackground(String... strs) {

            if (strs == null || strs.length != 1) {
                return null;
            }

            String filePath = strs[0];

            S3TaskResult result = new S3TaskResult();
            // Put the image data into S3.
            try {
                s3Client.createBucket(Constants.getPictureBucket());
                // Content type is determined by file extension.
                PutObjectRequest por = new PutObjectRequest(
                        Constants.getPictureBucket(), Constants.PICTURE_NAME + owner_id,
                        new java.io.File(filePath));
                Log.v("check", "S3putObjectTask doInBackground4");
                s3Client.putObject(por);
            } catch (Exception exception) {

                result.setErrorMessage(exception.getMessage());
            }

            return result;
        }

        protected void onPostExecute(S3TaskResult result) {

            dialog.dismiss();

            if (result.getErrorMessage() != null) {

                displayErrorAlert(
                        activity
                                .getString(R.string.upload_failure_title),
                        result.getErrorMessage());
            }
        }

    }



    public class S3GeneratePresignedUrlTask extends
            AsyncTask<Void, Void, S3TaskResult> {

        protected S3TaskResult doInBackground(Void... voids) {

            S3TaskResult result = new S3TaskResult();

            try {
                // Ensure that the image will be treated as such.
                ResponseHeaderOverrides override = new ResponseHeaderOverrides();
                override.setContentType("image/jpeg");

                // Generate the presigned URL.

                // Added an hour's worth of milliseconds to the current time.
                Date expirationDate = new Date(
                        System.currentTimeMillis() + 3600000);
                GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
                        Constants.getPictureBucket(), Constants.PICTURE_NAME + owner_id);
                urlRequest.setExpiration(expirationDate);
                urlRequest.setResponseHeaders(override);

                URL url = s3Client.generatePresignedUrl(urlRequest);

                result.setUri(Uri.parse(url.toURI().toString()));

            } catch (Exception exception) {

                result.setErrorMessage(exception.getMessage());
            }

            return result;
        }

        protected void onPostExecute(S3TaskResult result) {

            if (result.getErrorMessage() != null) {

                displayErrorAlert(
                        activity
                                .getString(R.string.browser_failure_title),
                        result.getErrorMessage());
            } else if (result.getUri() != null) {
                // Display in Browser.
//                startActivity(new Intent(Intent.ACTION_VIEW, result.getUri()));
            }
        }

    }

    public class S3TaskResult {
        String errorMessage = null;
        Uri uri = null;

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }
    }

    public class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                imageView.setImageBitmap(getRoundedShape(result));
            }
            System.out.println("finished");
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);

                if(stream != null) {
                    stream.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpsURLConnection.HTTP_OK ||
                        httpConnection.getResponseCode() == HttpsURLConnection.HTTP_NOT_MODIFIED ) {

                    stream = httpConnection.getInputStream();
                } else { // just in case..

                    //log.d("Surprize HTTP status was: " ,httpConnection.getResponseCode());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
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

}

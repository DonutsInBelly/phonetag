package based.phonetag;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.camera2.CameraDevice;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    static String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                //Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to :\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                sendToServer();
            } else if (resultCode == RESULT_CANCELED) {
                //User cancelled the image capture
                System.out.println("Image canceled.");
            } else {
                // Image capture failed, advise user
                Toast.makeText(this, "Image capture failed.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static void sendToServer() {
        Socket socket = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket("104.131.109.87", 9001);
            outputStream = socket.getOutputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            /* BEGIN BYTE CONVERSION */
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = 4;
            options.inPurgeable = true;
            Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

            /*
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            */
            bm.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);


            // bitmap object
            /*
            byte[] byteImage_photo = baos.toByteArray();
            */

            /* END BYTE CONVERSION */

            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
            outputStream.write(size);
            outputStream.write(byteArrayOutputStream.toByteArray());
            outputStream.flush();
            System.out.println("Flushed: " + System.currentTimeMillis());

            Thread.sleep(120000);
            System.out.println("Closing: " + System.currentTimeMillis());
            socket.close();
        } catch (Exception e) {

        }

    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void switchToCamera(View v) {
        dispatchTakePictureIntent();
    }

    public void switchToHint(View v) {
        return;
    }


    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(imageFileName+".jpg");

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;


    }

    private void dispatchTakePictureIntent() {
        File file = createImageFile();
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }
    /*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }
    */
    /*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager())!=null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println(ex);
            }
            // Continue only if the File was successfully created
            if (photoFile!=null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    */

    /** A safe way to get an instance of the Camera object. */
    /*public static CameraDevice getCameraInstance(){
        CameraDevice c = null;
        try {
            c = CameraDevice.; // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }*/
}

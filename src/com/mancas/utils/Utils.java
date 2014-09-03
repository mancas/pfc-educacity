package com.mancas.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mancas.models.RegisterModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Factory that contains useful common methods
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class Utils
{
    /**
     * Tag to specify the prefix of the photos taken by the application
     */
    private static final String JPEG_FILE_PREFIX = "IMG_";
    /**
     * Tag to specify the extension of the photos taken by the application
     */
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    /**
     * Regular expression that check the validity or not of an email account
     */
    private static final String EMAIL_PATTERN = 
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Method that resizes a listview based on his children
     * @param listView the ListView that must be dimensioned
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        BaseAdapter mAdapter = (BaseAdapter) listView.getAdapter(); 
        if (mAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View listItem = mAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * Creates a new image file
     * @param storageDir directory where the new file will be saved
     * @return the new image file
     * @throws IOException
     */
    public static File createImageFile(File storageDir) throws IOException
    {
        // Create an image file name
        File album = getAlbumDir(storageDir);
        File image = File.createTempFile(getFileName(), JPEG_FILE_SUFFIX, album);

        return image;
    }

    /**
     * Gets a file name based on the current date
     * @return a new file name
     */
    public static String getFileName()
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";

        return imageFileName;
    }

    /**
     * Creates a new album directory if not exists
     * @param storageDir
     * @return the file object associated to the album directory
     */
    public static File getAlbumDir(File storageDir)
    {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()){
                        Log.d("UTILS", "failed to create directory");
                        return null;
                    }
                }
            }
        }

        return storageDir;
    }

    /**
     * Sets a picture defined in filePath argument in an ImageView
     * @param filePath image absolute path that defined an image
     * @param view ImageView where the image will be displayed
     */
    public static void setPicture(String filePath, ImageView view)
    {
        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */
        int targetW = view.getWidth();
        int targetH = view.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        
        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH); 
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

        /* Associate the Bitmap to the ImageView */
        view.setImageBitmap(bitmap);
    }

    /**
     * Sets a square picture defined in filePath argument in an ImageView
     * @param filePath image absolute path that defined an image
     * @param view ImageView where the image will be displayed
     */
    public static void setSquarePicture(String filePath, ImageView view)
    {
        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */
        int targetW = ((View) view.getParent()).getWidth();
        int targetH = targetW;

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        
        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH); 
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

        /* Associate the Bitmap to the ImageView */
        view.setImageBitmap(bitmap);
    }

    /**
     * Gets the URI from a Bitmap image
     * @param context the context of the application
     * @param image the Bitmap image
     * @return the URI associated to the Bitmap image
     */
    public static Uri getImageUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(context.getContentResolver(), image, getFileName(), null);

        return Uri.parse(path);
    }

    /**
     * Gets the absolute path from an URI
     * @param context the context of the application
     * @param uri the URI that describing the file
     * @return the real path of the file
     */
    public static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null); 
        cursor.moveToFirst(); 
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 

        return cursor.getString(idx); 
    }

    /**
     * Gets the real path form a Bitmap image
     * @param context the context of the application
     * @param image the Bitmap image
     * @return the real path of the image
     */
    public static String getRealPathFromBitmap(Context context, Bitmap image)
    {
        Uri uri = getImageUri(context, image);

        return getRealPathFromURI(context, uri);
    }

    /**
     * Method that validates an email address
     * @param email the email to validate
     * @return true if the email argument matches the regular expression {@link #EMAIL_PATTERN}
     */
    public static boolean checkEmailAddress(String email)
    {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        
        return matcher.matches();
    }

    /**
     * Resize and returns a thumbnail of an image
     * @param path the absolute path of the image which is going to be decoded
     * @param width the width of the resulting thumbnail
     * @param height the height of the resulting thumbnail
     * @return a thumbnail of the image specified in path argument
     */
    public static Bitmap decodeFile(String path, int width, int height)
    {
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            int scale = 1;
            while (options.outWidth / scale / 2 >= width && options.outHeight / scale / 2 >= height) {
                scale *= 2;
            }
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.Options resultOptions = new BitmapFactory.Options();
            resultOptions.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(f), null, resultOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get the screen width
     * @param context the context of the application
     * @return the screen width
     */
    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point p = new Point();

        display.getSize(p);

        return p.x;
    }

    /**
     * Get the screen height
     * @param context the context of the application
     * @return the screen height
     */
    public static int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point p = new Point();

        display.getSize(p);

        return p.y;
    }

    /**
     * Generates a new TOKEN url to perform requests
     * @param url
     * @param clientId
     * @param clientSecret
     * @param grantType
     * @return the complete url
     */
    public static String generateTokenURL(String url, String clientId, String clientSecret,
            String grantType) {
        url += "?client_id=" +clientId + "&client_secret=" + clientSecret +
                "&grant_type=" + grantType;

        return url;
    }
}

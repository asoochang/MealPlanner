package scu.csci187.fall2018.mealtracker.Classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/*
    TODO: resize bitmap for faster processing
 */
public class ImageLoaderFromUrl extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public ImageLoaderFromUrl(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap pic = null;

        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            pic = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        //Bitmap resized = Bitmap.createScaledBitmap(yourBitmap, newWidth, newHeight, true);
        return pic;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}

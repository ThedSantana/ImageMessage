package com.jonathanmackenzie.imagemessage;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class DecodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        // Show the Up button in the action bar.
        setupActionBar();
        Intent intent = getIntent();
        ImageView iv = (ImageView) findViewById(R.id.imageViewInput);
        iv.setImageURI(intent.getData());
        String content = decodeImage(intent.getData());
        LinearLayout layout = (LinearLayout) findViewById(R.id.decodeOutput);
        if (content != null) {
            // Detect if the string is either 
            boolean image = content.startsWith("data:image/");
            if (image) {
                // Put the hidden image below
                ImageView ivout = new ImageView(this);
                byte[] decodedString = Base64.decode(content, Base64.DEFAULT);
                Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                iv.setImageBitmap(bm);
                layout.addView(ivout);
            } else {
                // Put the text message in
                TextView tv = new TextView(this);
                tv.setText(content);
                layout.addView(tv);
            }
        } else {
            TextView tv = new TextView(this);
            tv.setText("No hidden message found");
            layout.addView(tv);
        }

    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.decode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Take the image and return a string
     * Taken from http://web.mit.edu/~georgiou/www/steganography/
     * @return
     */
    private static String decodeImage(Uri image) {
        // return null if it doesn't start with !@#
        Bitmap bm = BitmapFactory.decodeFile(image.getPath());
        StringBuffer sb = new StringBuffer();
        int[] pixels = new int[bm.getHeight()*bm.getWidth()];
        bm.getPixels(pixels,0, 1, 0, 0, bm.getWidth(), bm.getHeight());
        for (int i = 0; i < bm.getWidth()*bm.getHeight()*4;) {
            int charCode = 0;
            int x = 0;
            int y = 0;
            charCode |= pixels[i] & 3;
            i++;
            if ((i & 3) == 3) {
                i++;
            }
            charCode |= (pixels[i] & 3) << 2;
            i++;
            if ((i & 3) == 3) {
                i++;
            }
            charCode |= (pixels[i] & 3) << 4;
            i++;
            if ((i & 3) == 3) {
                i++;
            }
            charCode |= (pixels[i] & 3) << 6;
            i++;
            if ((i & 3) == 3) {
                i++;
            }
            if (charCode == 0) {
                break;
            }
            sb.append((char)charCode);
        }
        if (sb.substring(0,3) != "@!$") {
            return null;
        }
        return sb.substring(3);
    }

}

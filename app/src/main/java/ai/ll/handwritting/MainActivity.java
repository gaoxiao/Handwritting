package ai.ll.handwritting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int PIXEL_WIDTH = 28;
    private TextView resText;
    private String expectNumber = getRandomEquation();
    private Bitmap emptyBmp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resText = (TextView) findViewById(R.id.textView);
        resText.setText(String.valueOf("draw " + expectNumber));
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        if (emptyBmp == null) {
            emptyBmp = Bitmap.createBitmap(bitmapImage.getWidth(), bitmapImage.getHeight(), bitmapImage.getConfig());
            Canvas canvas = new Canvas(emptyBmp);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(emptyBmp, 0, 0, null);
        }

        if (bitmapImage.sameAs(emptyBmp)) {
            Toast.makeText(getApplicationContext(), "Empty image!!! Didn't save.", Toast.LENGTH_SHORT).show();
            return;
        }

        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("saved_data", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, UUID.randomUUID() + ".png");
        File index = new File(directory, "index.txt");
        FileOutputStream fos = null;
        OutputStreamWriter myOutWriter = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            myOutWriter = new OutputStreamWriter(new FileOutputStream(index, true));
            myOutWriter.append(String.format("%s,%s\n", mypath.getName(), expectNumber));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                myOutWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getApplicationContext(), "Saved.", Toast.LENGTH_SHORT).show();
    }

    public void save(View view) {
        SignaturePad pad = findViewById(R.id.signature_pad);
        Bitmap bmp = pad.getSignatureBitmap();
        saveToInternalStorage(bmp);
        clear(view);
    }

    public void clear(View view) {
        SignaturePad pad = findViewById(R.id.signature_pad);
        expectNumber = getRandomEquation();
        resText.setText(expectNumber);
        pad.clear();
    }

    @SuppressLint("DefaultLocale")
    private String getRandomEquation() {
        String number = "NULL";
        Random random = new Random();
        int dice = random.nextInt(20);
        if (dice < 5) {
            number = "" + random.nextInt(10);
        } else if (dice < 7) {
            number = "" + random.nextInt(100);
        } else if (dice < 9) {
            number = "" + random.nextInt(1000);
        } else if (dice < 11) {
            number = "" + random.nextInt(1000);
        } else if (dice < 14) {
            number = String.format("%d.%d", random.nextInt(10), random.nextInt(10));
        } else if (dice < 15) {
            number = String.format("%d.%02d", random.nextInt(100), random.nextInt(100));
        } else if (dice < 18) {
            long a = (random.nextInt(9) + 1);
            long b = (random.nextInt(9) + 1);
            long gcm = gcm(a, b);
            number = String.format("%d/%d", a / gcm, b / gcm);

        } else if (dice < 20) {
            long a = (random.nextInt(99) + 1);
            long b = (random.nextInt(99) + 1);
            long gcm = gcm(a, b);
            number = String.format("%d/%d", a / gcm, b / gcm);
        }
        return number;
    }

    public static long gcm(long a, long b) {
        return b == 0 ? a : gcm(b, a % b);
    }
}

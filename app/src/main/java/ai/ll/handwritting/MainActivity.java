package ai.ll.handwritting;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
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
    private String expectNumber = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resText = (TextView) findViewById(R.id.textView);
        resText.setText(String.valueOf("draw " + expectNumber));
        //loadModel();
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {

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
        return directory.getAbsolutePath();
    }

    public void save(View view) {
        SignaturePad pad = findViewById(R.id.signature_pad);
        Bitmap bmp = pad.getSignatureBitmap();
        saveToInternalStorage(bmp);
        clear(view);
    }

    public void clear(View view) {
        SignaturePad pad = findViewById(R.id.signature_pad);
        Random random = new Random();
        int dice = random.nextInt(20);
        if (dice < 5) {
            expectNumber = "" + random.nextInt(10);
        } else if (dice < 7) {
            expectNumber = "" + random.nextInt(100);
        } else if (dice < 9) {
            expectNumber = "" + random.nextInt(1000);
        } else if (dice < 11) {
            expectNumber = "" + random.nextInt(1000);
        } else if (dice < 14) {
            expectNumber = "" + random.nextInt(10) + "." + random.nextInt(10);
        } else if (dice < 15) {
            expectNumber = "" + random.nextInt(100) + "." + random.nextInt(100);
        } else if (dice < 18) {
            long a = (random.nextInt(9) + 1);
            long b = (random.nextInt(9) + 1);
            long gcm = gcm(a, b);
            expectNumber = "" + (a / gcm) + "/" + (b / gcm);
        } else if (dice < 20) {
            long a = (random.nextInt(99) + 1);
            long b = (random.nextInt(99) + 1);
            long gcm = gcm(a, b);
            expectNumber = "" + (a / gcm) + "/" + (b / gcm);
        }
        resText.setText(expectNumber);
        pad.clear();
    }

    public static long gcm(long a, long b) {
        return b == 0 ? a : gcm(b, a % b);
    }


    //        try (FileOutputStream out = new FileOutputStream("1.png")) {
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//            Toast.makeText(getApplicationContext(), "Saved.", Toast.LENGTH_SHORT).show();
//            //init an empty string to fill with the classification output
//            String text = "";
//            float pixels[] = bitmapToFloatArray(bmp,28f,28f);
//            //for each classifier in our array
//            for (Classifier classifier : mClassifiers) {
//                //perform classification on the image
//                final Classification res = classifier.recognize(pixels);
//                //if it can't classify, output a question mark
//                if (res.getLabel() == null) {
//                    text += classifier.name() + ": ?\n";
//                } else {
//                    //else output its name
//                    text += String.format("%s: %s, %f\n", classifier.name(), res.getLabel(),
//                            res.getConf());
//                }
//            }
//            resText.setText(text);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    public float[] getPixelData(Bitmap mOffscreenBitmap) {
//        if (mOffscreenBitmap == null) {
//            return null;
//        }
//
//        int width = mOffscreenBitmap.getWidth();
//        int height = mOffscreenBitmap.getHeight();
//
//        // Get 28x28 pixel data from bitmap
//        int[] pixels = new int[width * height];
//        mOffscreenBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
//
//        float[] retPixels = new float[pixels.length];
//        for (int i = 0; i < pixels.length; ++i) {
//            // Set 0 for white and 255 for black pixel
//            int pix = pixels[i];
//            int b = pix & 0xff;
//            retPixels[i] = (float)((0xff - b)/255.0);
//        }
//        return retPixels;
//    }

//    private float[] bitmapToFloatArray(Bitmap bitmap, Float rx, Float ry) {
//        int height = bitmap.getHeight();
//        int width = bitmap.getWidth();
//        float scaleWidth = rx / width;
//        float scaleHeight = ry / height;
//        Matrix matrix = new Matrix();
//        matrix.postScale(scaleWidth, scaleHeight);
//        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//        height = bitmap.getHeight();
//        width = bitmap.getWidth();
//        float [] result = new float [height * width];
//        int k=0;
//        for (int col =0;col<height; col++) {
//            for (int row=0; row<width;row++) {
//                int r = bitmap.getPixel(col, row);
//                result[k++] = r / 255.0f;
//            }
//        }
//        return result;
//    }
//
//    private void loadModel() {
//        //The Runnable interface is another way in which you can implement multi-threading other than extending the
//        // //Thread class due to the fact that Java allows you to extend only one class. Runnable is just an interface,
//        // //which provides the method run.
//        // //Threads are implementations and use Runnable to call the method run().
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //add 2 classifiers to our classifier arraylist
//                    //the tensorflow classifier and the keras classifier
//                    mClassifiers.add(
//                            TensorflowClassifier.create(getAssets(), "TensorFlow",
//                                    "opt_mnist_convnet-tf.pb", "labels.txt", PIXEL_WIDTH,
//                                    "input", "output", true));
//                    mClassifiers.add(
//                            TensorflowClassifier.create(getAssets(), "Keras",
//                                    "opt_mnist_convnet-keras.pb", "labels.txt", PIXEL_WIDTH,
//                                    "conv2d_1_input", "dense_2/Softmax", false));
//                } catch (final Exception e) {
//                    //if they aren't found, throw an error!
//                    throw new RuntimeException("Error initializing classifiers!", e);
//                }
//            }
//        }).start();
//    }

}

package ai.ll.handwritting;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int PIXEL_WIDTH = 28;
    private TextView resText;
    private String expectNumber = getRandomEquation();
    private Bitmap emptyBmp = null;
    private File mypath;
    private String filenumber;
    private String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resText = (TextView) findViewById(R.id.textView);
        resText.setText(String.valueOf("draw " + expectNumber));
    }

    private void saveToInternalStorage(final Bitmap bitmapImage) {
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
        String filename = UUID.randomUUID() + ".png";
        mypath = new File(directory, filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            filenumber = expectNumber;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getApplicationContext(), "Saved.", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {   //use another thread to upload file

                    SchemeRegistry schemeRegistry = new SchemeRegistry();
                    schemeRegistry.register(new Scheme("https",
                            SSLSocketFactory.getSocketFactory(), 443));
                    HttpParams params = new BasicHttpParams();
                    SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);
//                  HttpClient client = new DefaultHttpClient(mgr, params);
//                  HttpPost post = new HttpPost("https://app.gkid.com/gkids/ai/upload/img");
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://192.168.2.111:9090/gkids/ai/upload/img");
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entityBuilder.addTextBody("type", "handwritten");
                    entityBuilder.addTextBody("label", filenumber);
                    entityBuilder.addBinaryBody("img", mypath);
                    HttpEntity entity = entityBuilder.build();
                    post.setEntity(entity);
                    HttpResponse response = client.execute(post);
                    HttpEntity httpEntity = response.getEntity();
                    result = EntityUtils.toString(httpEntity);
                    mypath.delete();
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Looper.loop();
                }
                Log.e("result", result);
            }
        }).start();
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

package ai.ll.handwritting;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void save(View view) {
        SignaturePad pad = findViewById(R.id.signature_pad);
        Bitmap bmp = pad.getSignatureBitmap();

        try (FileOutputStream out = openFileOutput("test.png", Context.MODE_PRIVATE)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            Toast.makeText(getApplicationContext(), "Saved.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear(View view) {
        SignaturePad pad = findViewById(R.id.signature_pad);
        pad.clear();
    }
}

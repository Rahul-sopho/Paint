package org.Paint.application.paint;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.owner.paint.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private CanvasView canvasView;

    Button save;
    Button share;
    String currentImage = "";
    private static final int MY_PERMISSION_REQUEST = 1;

    public void clearCanvas(View v) {
        canvasView.clearCanvas();
        share.setEnabled(false);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED )
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);

            }
        }
        else {
            //do nothing
        }

        canvasView = (CanvasView) findViewById(R.id.canvas);
        save = (Button) findViewById(R.id.button2);
        share= (Button) findViewById(R.id.button3);

        share.setEnabled(false);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View content = findViewById(R.id.canvas);
                Bitmap bitmap = getScreenShot(content);
                currentImage = "paint" + System.currentTimeMillis()+".png";
                store(bitmap,currentImage);
                share.setEnabled(true);

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareImage(currentImage);
            }
        });








    }


    public static Bitmap getScreenShot(View view)
    {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void store(Bitmap bm, String fileName)
    {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PAINT";
        File dir = new File(dirPath);
        if(!dir.exists())
        {
            dir.mkdir();
        }
        File file = new File(dirPath,fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Saved !",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            Toast.makeText(this, "Error Saving !",Toast.LENGTH_SHORT).show();
        }

    }



    public void shareImage(String fileName)
    {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PAINT";
        Uri uri = Uri.fromFile(new File(dirPath,fileName));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT,"");
        intent.putExtra(Intent.EXTRA_TEXT,"");
        intent.putExtra(Intent.EXTRA_STREAM,uri);

        try{
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (ActivityNotFoundException e){
            Toast.makeText(this,"No sharing app found",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case MY_PERMISSION_REQUEST :
            {
                if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED);
                }
                else
                {
                    Toast.makeText(this,"No Permission Granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }




    }
}
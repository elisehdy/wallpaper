package tree.hacks.wallpaper;


import android.util.DisplayMetrics;
import android.view.View;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.IOException;

import tree.hacks.wallpaper.R;

public class SecondActivity extends AppCompatActivity {

    private Button setWallpaper;

    ImageView currWallpaper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        currWallpaper = (ImageView)findViewById(R.id.currWallpaper);
        setWallpaper = (Button) findViewById(R.id.changeWallpaper);
        setWallpaper.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,0);
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView userName = (TextView) findViewById(R.id.userName);
        String text = getIntent().getExtras().getString("userName");
        userName.setText("hi " + text + "!");

        TextView wallpaperGroupNumber = (TextView) findViewById(R.id.wallpaperGroupNumber);
        text = getIntent().getExtras().getString("groupNum");
        wallpaperGroupNumber.setText("you are in group " + text);


        Button leaveGroup = (Button) findViewById(R.id.leaveGroup);
        Button confirmLeave = (Button) findViewById(R.id.confirmLeave);
        Button cancelLeave = (Button) findViewById(R.id.cancelLeave);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        leaveGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                confirmLeave.setVisibility(View.VISIBLE);
                cancelLeave.setVisibility(View.VISIBLE);
            }
        });

        confirmLeave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // leave the group in the database
                Intent returnBtn = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(returnBtn);
            }
        });

        cancelLeave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                confirmLeave.setVisibility(View.INVISIBLE);
                cancelLeave.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    // make it so you can't accidentally leave the group by just pressing back
    public void onBackPressed() {
        return;
    }

    public void returnToMain() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                currWallpaper.setImageBitmap(bitmap);
                WallpaperManager wallpaperChanger = WallpaperManager.getInstance(getApplicationContext());
                try {
                    wallpaperChanger.setBitmap(bitmap);
                    Toast.makeText(SecondActivity.this, "Wallpaper Changed", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}
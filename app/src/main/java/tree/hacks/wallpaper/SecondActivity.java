package tree.hacks.wallpaper;


import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SecondActivity extends AppCompatActivity {

    private Button setWallpaper;
    TextView userName;
    TextView wallpaperGroupNumber;
    Button leaveGroup;
    Button confirmLeave;
    Button cancelLeave;
    Button viewWallpaper;
    Button viewMembers;
    TextView viewWallpaperError;
    boolean wallpaperChanged;
    Bitmap bitmap;
    Uri targetUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        wallpaperChanged = false;
        setWallpaper = (Button) findViewById(R.id.changeWallpaper);
        setWallpaper.setOnClickListener(v -> {
            viewWallpaperError.setText("");
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,0);
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        userName = (TextView) findViewById(R.id.userName);
        String name = getIntent().getExtras().getString("userName");
        userName.setText("hi " + name + "!");

        wallpaperGroupNumber = (TextView) findViewById(R.id.wallpaperGroupNumber);
        String groupNum = getIntent().getExtras().getString("groupNum");
        wallpaperGroupNumber.setText("you are in group " + groupNum);

        viewWallpaperError = (TextView) findViewById(R.id.viewWallpaperErrorText);
        leaveGroup = (Button) findViewById(R.id.leaveGroup);
        confirmLeave = (Button) findViewById(R.id.confirmLeave);
        cancelLeave = (Button) findViewById(R.id.cancelLeave);
        viewWallpaper = (Button) findViewById(R.id.viewWallpaperBtn);
        viewMembers = (Button) findViewById(R.id.viewMembersBtn);

        leaveGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                confirmLeave.setVisibility(View.VISIBLE);
                cancelLeave.setVisibility(View.VISIBLE);
            }
        });

        confirmLeave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // leave the group in the database
                viewWallpaperError.setText("");
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

        viewWallpaper.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (! wallpaperChanged) {
                    viewWallpaperError.setText("You have not set a wallpaper yet!");
                    return;
                }
                viewWallpaperError.setText("");

                Intent startIntent = new Intent(getApplicationContext(), tree.hacks.wallpaper.ViewWallpaper.class);
                startIntent.putExtra("uri", targetUri.toString());
                startActivity(startIntent);
            }
        });

        viewMembers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), tree.hacks.wallpaper.ViewGroupMembers.class);
                startIntent.putExtra("groupNum", groupNum);
                startActivity(startIntent);
                viewWallpaperError.setText("");
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
            targetUri = data.getData();
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                WallpaperManager wallpaperChanger = WallpaperManager.getInstance(getApplicationContext());
                try {
                    wallpaperChanger.setBitmap(bitmap);
                    Toast.makeText(SecondActivity.this, "Wallpaper Changed", Toast.LENGTH_LONG).show();
                    wallpaperChanged = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}
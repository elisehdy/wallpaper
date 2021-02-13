package tree.hacks.wallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import tree.hacks.wallpaper.R;

public class SecondActivity extends AppCompatActivity {

    private Button setWallpaper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        setWallpaper = (Button) findViewById(R.id.changeWallpaper);
        setWallpaper.setOnClickListener(v -> {
            WallpaperManager wallpaperChanger = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperChanger.setResource(+R.drawable.testwallpaper);
                Toast.makeText(SecondActivity.this, "Wallpaper Changed", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        TextView userName = (TextView) findViewById(R.id.userName);
        String text = getIntent().getExtras().getString("userName");
        userName.setText("hi " + text + "!");

        TextView wallpaperGroupNumber = (TextView) findViewById(R.id.wallpaperGroupNumber);
        text = getIntent().getExtras().getString("groupNum");
        userName.setText("you are in group " + text);
    }
}
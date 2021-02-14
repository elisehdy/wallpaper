package tree.hacks.wallpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

public class ViewWallpaper extends AppCompatActivity {

    ImageView currWallpaper;
    Button returnToGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        currWallpaper = (ImageView) findViewById(R.id.currWallpaper);
        returnToGroup = (Button) findViewById(R.id.returnToGroupBtn);

        String groupNumText = getIntent().getExtras().getString("groupNum");
        db.collection("rooms")
                .whereEqualTo("group number", groupNumText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                Glide.with(ViewWallpaper.this)
                                        .load(Objects.requireNonNull(document.getData().get("wallpaper")).toString())
                                        .into(currWallpaper);
                            }
                        } else {
                            Toast.makeText(ViewWallpaper.this, "Failed to retrieve data", Toast.LENGTH_LONG).show();
                        }
                    }
                });


        returnToGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
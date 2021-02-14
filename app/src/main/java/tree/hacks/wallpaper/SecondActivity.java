package tree.hacks.wallpaper;



import android.util.DisplayMetrics;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;


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
    private Uri imageUri;
    ImageButton image;
    String nameText;
    String groupNumText;
    Uri downloadUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        wallpaperChanged = false;
        setWallpaper = (Button) findViewById(R.id.changeWallpaper);
        setWallpaper.setOnClickListener(v -> {
            viewWallpaperError.setText("");
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,0);
        });


        userName = (TextView) findViewById(R.id.userName);
        nameText = getIntent().getExtras().getString("userName");
        userName.setText("hi " + nameText + "!");

        wallpaperGroupNumber = (TextView) findViewById(R.id.wallpaperGroupNumber);
        groupNumText = getIntent().getExtras().getString("groupNum");
        wallpaperGroupNumber.setText("you are in group " + groupNumText);


        viewWallpaperError = (TextView) findViewById(R.id.viewWallpaperErrorText);
        leaveGroup = (Button) findViewById(R.id.leaveGroup);
        confirmLeave = (Button) findViewById(R.id.confirmLeave);
        cancelLeave = (Button) findViewById(R.id.cancelLeave);
        viewWallpaper = (Button) findViewById(R.id.viewWallpaperBtn);
        viewMembers = (Button) findViewById(R.id.viewMembersBtn);

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
                DocumentReference room = db.collection("rooms").document(groupNumText);
                room.update("members", FieldValue.arrayRemove(nameText));

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
                startIntent.putExtra("uri", imageUri.toString());
                startActivity(startIntent);
            }
        });

        viewMembers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), tree.hacks.wallpaper.ViewGroupMembers.class);
                startIntent.putExtra("groupNum", groupNumText);
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if(resultCode == RESULT_OK){
            imageUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                WallpaperManager wallpaperChanger = WallpaperManager.getInstance(getApplicationContext());
                try {
                    wallpaperChanger.setBitmap(bitmap);

                    StorageReference storageRef = storage.getReference();
                    StorageReference wallRef = storageRef.child("images/"+imageUri.getLastPathSegment());
                    UploadTask uploadTask = wallRef.putFile(imageUri);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return wallRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                wallpaperChanged = true;
                                Uri downloadUri = task.getResult();
                                CollectionReference group = db.collection("rooms");
                                Map<String, Object> image = new HashMap<>();
                                image.put("wallpaper",  downloadUri.toString());
                                image.put("owner", nameText);
                                group.document(groupNumText).update(image);
                                Toast.makeText(SecondActivity.this, "Wallpaper Changed", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SecondActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}
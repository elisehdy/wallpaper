package tree.hacks.wallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SecondActivity extends AppCompatActivity {

    private Button setWallpaper;
    TextView userName;
    TextView wallpaperGroupNumber;
    Button leaveGroup;
    Button confirmLeave;
    Button cancelLeave;
    Button viewWallpaper;
    Button viewMembers;
    boolean wallpaperChanged;
    Bitmap bitmap;
    private Uri imageUri;
    ImageButton image;
    String nameText;
    String groupNumText;
    Uri downloadUri;
    String notifId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        wallpaperChanged = false;
        setWallpaper = (Button) findViewById(R.id.changeWallpaper);
        setWallpaper.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,0);
        });


        userName = (TextView) findViewById(R.id.userName);
        nameText = getIntent().getExtras().getString("userName");
        userName.setText(nameText + "!");

        wallpaperGroupNumber = (TextView) findViewById(R.id.wallpaperGroupNumber);
        groupNumText = getIntent().getExtras().getString("groupNum");
        wallpaperGroupNumber.setText(groupNumText);

        leaveGroup = (Button) findViewById(R.id.leaveGroup);
        confirmLeave = (Button) findViewById(R.id.confirmLeave);
        cancelLeave = (Button) findViewById(R.id.cancelLeave);
        viewWallpaper = (Button) findViewById(R.id.viewWallpaperBtn);
        viewMembers = (Button) findViewById(R.id.viewMembersBtn);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        db.collection("rooms")
                .whereEqualTo("group number", groupNumText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                try {
                                    Glide.with(SecondActivity.this)
                                            .asBitmap()
                                            .load(Objects.requireNonNull(document.getData().get("wallpaper")).toString())
                                            .into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                                    WallpaperManager wallpaperChanger = WallpaperManager.getInstance(getApplicationContext());
                                                    try {
                                                        wallpaperChanger.setBitmap(bitmap);
                                                        Toast.makeText(SecondActivity.this, "Wallpaper changed", Toast.LENGTH_LONG).show();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                                }
                                            });
                                } catch (NullPointerException e) {

                                }

                            }
                        } else {
                            Toast.makeText(SecondActivity.this, "Failed to retrieve data", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic(groupNumText)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SecondActivity.this, "subscription failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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


                FirebaseMessaging.getInstance().unsubscribeFromTopic(groupNumText)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SecondActivity.this, "unsubscription failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                

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
                Intent startIntent = new Intent(getApplicationContext(), tree.hacks.wallpaper.ViewWallpaper.class);
                startIntent.putExtra("groupNum", groupNumText);
                startActivity(startIntent);
            }
        });

        viewMembers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), tree.hacks.wallpaper.ViewGroupMembers.class);
                startIntent.putExtra("groupNum", groupNumText);
                startActivity(startIntent);
            }
        });

        final DocumentReference docRef = db.collection("rooms").document(groupNumText);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(SecondActivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Glide.with(SecondActivity.this)
                            .asBitmap()
                            .load(Objects.requireNonNull(snapshot.getData().get("wallpaper")).toString())
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                    WallpaperManager wallpaperChanger = WallpaperManager.getInstance(getApplicationContext());
                                    try {
                                        wallpaperChanger.setBitmap(bitmap);
                                        Toast.makeText(SecondActivity.this, "Wallpaper changed", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                } else {

                }
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
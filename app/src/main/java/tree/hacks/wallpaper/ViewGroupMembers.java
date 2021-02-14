package tree.hacks.wallpaper;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class ViewGroupMembers extends AppCompatActivity {

    private static final String TAG = "accessed data";
    String[] groupMembers;
    String owner;
    ListView myListView;
    String groupNum;
    Button returnToGroup;
    boolean foundValues;
    Object lockBoolean = new Object();

    FirebaseFirestore db;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_members);

        groupNum = getIntent().getExtras().getString("groupNum");
        // groupMembers = getGroupMembers(groupNum);
        // wallpaperOwner = getWallpaperOwner(groupNum);
        returnToGroup = (Button) findViewById(R.id.returnToGroup);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        owner = ""; // initialize value
        foundValues = false;
        db.collection("rooms")
                .whereEqualTo("group number", groupNum)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                synchronized (lockBoolean) {
                                    groupMembers = convertToArray((List<String>) document.getData().get("members"));
                                    owner = (String) document.getData().get("owner");
                                    foundValues = true;
                                    createDisplayList();
                                }
                            }
                        } else {
                            Toast.makeText(ViewGroupMembers.this, "Failed to retrieve data", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        returnToGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void createDisplayList() {
        String[] wallpaperOwner = getWallpaperOwner(groupMembers, owner);
        myListView = (ListView) findViewById(R.id.myListView);
        ItemAdapter itemAdapter = new ItemAdapter(this, groupMembers, wallpaperOwner);
        myListView.setAdapter(itemAdapter);
        System.out.println("stuff happened");
    }

    private String[] getWallpaperOwner(String[] members, String owner) {
        String[] ownerList = new String[members.length];
        for (int i = 0; i < members.length; i++) {
            if (members[i].equals(owner)) {
                ownerList[i] = "current wallpaper owner";
                return ownerList;
            }
        }
        return ownerList;
    }

    private String[] convertToArray(List<String> stringList) {
        String[] newArr = new String[stringList.size()];

        for (int i = 0; i < stringList.size(); i++) {
            newArr[i] = stringList.get(i);
        }

        return newArr;
    }
}
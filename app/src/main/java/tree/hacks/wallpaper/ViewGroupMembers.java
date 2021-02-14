package tree.hacks.wallpaper;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ViewGroupMembers extends AppCompatActivity {

    private static final String TAG = "accessed data";
    String[] groupMembers;
    String owner;
    ListView myListView;
    String groupNum;
    Button returnToGroup;

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

//        db.collection("rooms")
//                .whereEqualTo("group number", groupNum)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            groupMembers = (String[]) document.getData().get("members");
//                            System.out.println("done!");
//                            owner = (String) document.getData().get("owner");
//                        }
//                    }
//                });
        String [] wallpaperOwner = getWallpaperOwner(groupMembers, owner);

        myListView = (ListView) findViewById(R.id.myListView);
        ItemAdapter itemAdapter = new ItemAdapter(this, groupMembers, wallpaperOwner);
        myListView.setAdapter(itemAdapter);

        returnToGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
}
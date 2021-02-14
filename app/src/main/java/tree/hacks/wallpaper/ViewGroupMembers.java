package tree.hacks.wallpaper;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewGroupMembers extends AppCompatActivity {

    String[] groupMembers;
    String[] wallpaperOwner;
    ListView myListView;
    String groupNum;
    Button returnToGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_members);

        groupNum = getIntent().getExtras().getString("groupNum");
        groupMembers = getGroupMembers(groupNum);
        wallpaperOwner = getWallpaperOwner(groupNum);
        returnToGroup = (Button) findViewById(R.id.returnToGroup);

        myListView = (ListView) findViewById(R.id.myListView);
        ItemAdapter itemAdapter = new ItemAdapter(this, groupMembers, wallpaperOwner);
        myListView.setAdapter(itemAdapter);

        returnToGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private String[] getGroupMembers(String groupNum) {
        String[] members = {"Alice", "Bob", "Cindy", "Doug", "Earl", "Frank", "Greg", "Holly"};

        return members;
    }

    private String[] getWallpaperOwner(String groupNum) {
        int indexOfWallpaperOwner = 1;
        String[] ownerList = new String[8];

        ownerList[indexOfWallpaperOwner] = "current wallpaper owner";

        return ownerList;
    }
}
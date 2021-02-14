package tree.hacks.wallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText nameText;
    EditText groupNumber;
    TextView messageText;
    Button enterGroupBtn;
    Button createGroupBtn;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameText = (EditText) findViewById(R.id.nameText);
        groupNumber = (EditText) findViewById(R.id.groupNumber);
        messageText = (TextView) findViewById(R.id.messageText);
        db = FirebaseFirestore.getInstance();
        enterGroupBtn = (Button) findViewById(R.id.enterGroupBtn);
        createGroupBtn = (Button) findViewById(R.id.createGroupBtn);



        enterGroupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String groupNum = groupNumber.getText().toString();

                if (name.length() == 0 || groupNumber.length() != 6) {
                    messageText.setText("Make sure you have a name and group code is valid!");
                    return;
                }

                db.collection("rooms")
                        .whereEqualTo("group number", groupNum)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    boolean matched = false;
                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                        matched = true;
                                    }
                                    if (matched == true) {
                                        joinRoom();
                                    }
                                    else {
                                        messageText.setText("Room not found.");
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to retrieve data", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String newCode = (int) ((Math.random() * (999999 - 100000)) + 100000) + "";
                createRoom(newCode);
            }
        });
    }

    private void joinRoom() {
        String name = nameText.getText().toString();
        String groupNum = groupNumber.getText().toString();

        DocumentReference room = db.collection("rooms").document(groupNum);
        room.update("members", FieldValue.arrayUnion(name));

        Intent startIntent = new Intent(getApplicationContext(), SecondActivity.class);
        messageText.setText(""); // clear error message from beginning
        // show how to pass information to another activity
        startIntent.putExtra("userName", name);
        startIntent.putExtra("groupNum", groupNum);
        startActivity(startIntent);
    }

    private void createRoom(String code) {
        String name = nameText.getText().toString();
        String groupNum = code;
        if (name.length() == 0) {
            messageText.setText("Don't forget your name!");
            return;
        }
        groupNumber.setText(groupNum);

        CollectionReference group = db.collection("rooms");
        Map<String, Object> data = new HashMap<>();
        data.put("group number", groupNum);
        ArrayList<String> members = new ArrayList<String>();
        members.add(name);
        data.put("members", members);
        data.put("wallpaper", "");
        group.document(groupNum).set(data);
        Intent startIntent = new Intent(getApplicationContext(), tree.hacks.wallpaper.SecondActivity.class);
        messageText.setText(""); // clear error message from beginning
        // show how to pass information to another activity
        startIntent.putExtra("userName", name);
        startIntent.putExtra("groupNum", groupNum);
        startActivity(startIntent);
    }

}
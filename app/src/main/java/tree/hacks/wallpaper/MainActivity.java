package tree.hacks.wallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import tree.hacks.wallpaper.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText nameText = (EditText) findViewById(R.id.nameText);
        EditText groupNumber = (EditText) findViewById(R.id.groupNumber);
        TextView messageText = (TextView) findViewById(R.id.messageText);
        Button secondActivityBtn = (Button) findViewById(R.id.secondActivityBtn);
        secondActivityBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String groupNum = groupNumber.getText().toString();
                if (! validCombo(name, groupNum)) {
                    messageText.setText("make sure you have a name and group code is valid!");
                    return;
                }

                Intent startIntent = new Intent(getApplicationContext(), tree.hacks.wallpaper.SecondActivity.class);
                messageText.setText(""); // clear error message from beginning
                // show how to pass information to another activity
                startIntent.putExtra("userName", name);
                startIntent.putExtra("groupNum", groupNum);
                startActivity(startIntent);
            }
        });
    }

    // returns that whether name and groupCode make a valid combo
    private boolean validCombo(String name, String groupCode) {
        return name.length() > 0 && groupCode.length() == 6;

    }
}
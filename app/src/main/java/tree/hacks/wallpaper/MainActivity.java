package tree.hacks.wallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText nameText = (EditText) findViewById(R.id.nameText);
        EditText groupNumber = (EditText) findViewById(R.id.groupNumber);
        TextView messageText = (TextView) findViewById(R.id.messageText);
        Button enterGroupBtn = (Button) findViewById(R.id.enterGroupBtn);
        enterGroupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String groupNum = groupNumber.getText().toString();
                if (name.length() == 0 || groupNumber.length() != 6) {
                    messageText.setText("Make sure you have a name and group code is valid!");
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

        Button createGroupBtn = (Button) findViewById(R.id.createGroupBtn);
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String groupNum = generateCode();
                if (name.length() == 0) {
                    messageText.setText("Don't forget your name!");
                    return;
                }
                groupNumber.setText(groupNum);
                Intent startIntent = new Intent(getApplicationContext(), tree.hacks.wallpaper.SecondActivity.class);
                messageText.setText(""); // clear error message from beginning
                // show how to pass information to another activity
                startIntent.putExtra("userName", name);
                startIntent.putExtra("groupNum", groupNum);
                startActivity(startIntent);
            }
        });
    }

    private String generateCode() {
        return (int) ((Math.random() * (999999 - 100000)) + 100000) + "";
    }


}
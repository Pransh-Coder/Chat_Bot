package com.proxima.chatty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    TextView textView;
    EditText edtMsg;
    Button btnSend;
    ArrayList<FirebaseTextMessage> conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.linearLayout);
        btnSend = findViewById(R.id.btnSend);
        edtMsg = findViewById(R.id.edtMsg);

        final LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout.LayoutParams params1 = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        conversation = new ArrayList<>();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView = new TextView(MainActivity.this);
                textView.setText(edtMsg.getText().toString());
                textView.setBackgroundResource(R.drawable.border);
                textView.setTextSize(15);

                conversation.add(FirebaseTextMessage.createForLocalUser(edtMsg.getText().toString(), System.currentTimeMillis()));
                conversation.add(FirebaseTextMessage.createForRemoteUser(edtMsg.getText().toString(), System.currentTimeMillis(), "8448338860"));

                params.gravity = Gravity.END;           // when user enters the text appears at the right most
                textView.setLayoutParams(params);
                edtMsg.setText("");
                linearLayout.addView(textView);
                FirebaseApp.initializeApp(MainActivity.this);

                FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
                smartReply.suggestReplies(conversation)
                        .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                            @Override
                            public void onSuccess(SmartReplySuggestionResult result) {
                                if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                                    Toast.makeText(MainActivity.this, "Language not supported ", Toast.LENGTH_SHORT).show();
                                    // The conversation's language isn't supported, so the
                                    // the result doesn't contain any suggestions.
                                }
                                else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                                    // Task completed successfully

                                    String replyText="";
                                    for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                        replyText = suggestion.getText();

                                    }
                                    textView = new TextView(MainActivity.this);
                                    textView.setText(replyText);
                                    textView.setBackgroundResource(R.drawable.border1);

                                    params1.gravity = Gravity.START;        // when chat Bot answers our question it appears on left most side
                                    textView.setTextSize(15);
                                    textView.setLayoutParams(params1);
                                    linearLayout.addView(textView);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });
            }
        });

    }
}

package com.witness.gov.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.witness.gov.R;
import com.witness.gov.data.UserInfo;

public class SignUpActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private static final String LOG_TAG = "SignUpActivity";

    private EditText email;
    private EditText pw;
    private EditText userName;
    private Spinner field;
    private String type;
    private EditText location;
    private Button enter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initUI();

        enter=(Button)findViewById(R.id.complete);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserInfo userInfo=isFilled();

                if(userInfo!=null) {
                    addNewUsers(userInfo);
                }
            }
        });
    }

    public void initUI(){
        email=(EditText)findViewById(R.id.email);
        pw=(EditText)findViewById(R.id.password);
        userName=(EditText)findViewById(R.id.username);

        field=(Spinner)findViewById(R.id.field);

        ArrayAdapter fieldAdapter=ArrayAdapter.createFromResource(this, R.array.field, android.R.layout.simple_spinner_item);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        field.setOnItemSelectedListener(this);
        field.setAdapter(fieldAdapter);


        location=(EditText)findViewById(R.id.location);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        type=parent.getItemAtPosition(position).toString();
        //Toast.makeText(SignUpActivity.this, "onItemSelected",
        //      Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // type=parent.getItemAtPosition(0).toString();
        //Toast.makeText(SignUpActivity.this, "onNothingSelected",
        //      Toast.LENGTH_SHORT).show();
    }

    public UserInfo isFilled(){

       UserInfo userInfo=new UserInfo();

        if(email.getText().toString().equals("")){
            email.setHint("insert Email");
            return null;
        }

        userInfo.setEmail(email.getText().toString());

        if(pw.getText().toString().equals("")){
            pw.setHint("insert PassWord");
            return null;
        }

        userInfo.setPw(pw.getText().toString());

        if(userName.getText().toString().equals("")){
            userName.setHint("insert UserName");
            return null;
        }

        userInfo.setUserName(userName.getText().toString());
        userInfo.setField(type);

        if(location.getText().toString().equals("")){
            location.setHint("insert Location");
            return null;
        }

        userInfo.setLocation(location.getText().toString());
        userInfo.setState("not working");

        return userInfo;
    }

    public void addNewUsers(final UserInfo info){

        final FirebaseAuth mAuth= FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), pw.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            addNewData(info,user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed. Please try again",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    public void addNewData(final UserInfo userInfo, final FirebaseUser user){
        String uid=email.getText().toString().replace('.', '_');
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference("app_users")
                    .push().setValue(userInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        FirebaseMessaging.getInstance().subscribeToTopic(userInfo.getLocation())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignUpActivity.this, "success Sign Up", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SignUpActivity.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                                    }
                                });
                        }
                    });
    }
}

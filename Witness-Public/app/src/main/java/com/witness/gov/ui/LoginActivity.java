package com.witness.gov.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.witness.gov.PlayRTCActivity;
import com.witness.gov.R;
import com.witness.gov.data.UserInfo;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity{

    private FirebaseAuth mAuth;

    private static final String LOG_TAG = "LoginActivity";
    private EditText email;
    private EditText pw;
    private Button login;
    private TextView signUp;
    private TextView forgetPw;

    //popupForgetPassword
    private LayoutInflater layoutInflater;
    private PopupWindow popup;
    private int width;
    private int height;

    //intent
    private UserInfo userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFunction();

    }

    public void initFunction(){
        mAuth = FirebaseAuth.getInstance();

        email=(EditText)findViewById(R.id.login_email);
        pw=(EditText)findViewById(R.id.login_password);
        login=(Button)findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals("")){
                    email.setHint("insert Email");
                }else if(pw.getText().toString().equals("")){
                    pw.setHint("insert PassWord");
                }else{
                    checkAppUser(email.getText().toString());
                }
            }
        });

        signUp=(TextView)findViewById(R.id.sign_up);
        signUp.setPaintFlags(signUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입
                startActivityForResult(new Intent(getApplicationContext(), SignUpActivity.class),100);
            }
        });

        forgetPw=(TextView)findViewById(R.id.forget_pw);
        forgetPw.setPaintFlags(forgetPw.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        forgetPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupForget();
            }
        });

    }

    public void checkAppUser(final String email){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference=database.getReference("app_users");

        reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot user : dataSnapshot.getChildren()){
                            String _email=user.child("email").getValue().toString();
                            if(_email.equals(email)){
                                userInfo=new UserInfo(user);
                                Log.i("UID", userInfo.getUid());
                               reference.child(userInfo.getUid())
                                        .child("state")
                                        .setValue("working")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                signIn(userInfo);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Please try it again",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void signIn(final UserInfo info){
        Log.e(LOG_TAG, info.getEmail()+" : "+info.getPw());
        mAuth.signInWithEmailAndPassword(info.getEmail(), info.getPw())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(LOG_TAG, "signInWithEmail:success");
                //FirebaseUser user=mAuth.getCurrentUser();
                updateUI(info);

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If sign in fails, display a message to the user.
                Log.w(LOG_TAG, "signInWithEmail:failure", e.getCause());
                updateUI(null);
            }
        });
    }

    public void popupForget(){
       Display display =((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width=display.getWidth();
        height=display.getHeight();
        layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout forgetLayout=(LinearLayout)findViewById(R.id.forget_layout);
        final View view = (View)layoutInflater.inflate(R.layout.forgetpw_popup, forgetLayout);

        popup=new PopupWindow(this);
        popup.setBackgroundDrawable(getDrawable(R.drawable.rounded));
        popup.setHeight((int)(height*0.2));
        popup.setWidth((int)(width*0.8));
        popup.setContentView(view);
        popup.setFocusable(true);
        popup.showAtLocation(view, Gravity.NO_GRAVITY, (int)(width*0.1), (int)(height*0.3));

       view.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String email=((EditText)view.findViewById(R.id.auth_email)).getText().toString();
               sendEmail(email);
           }
       });

    }

    public void sendEmail(String emailAddress){
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                        popup.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "메일 보내기 실패!", Toast.LENGTH_LONG).show();
            }
        });


    }

    public void updateUI(final UserInfo user){
        if(user!=null){
            // 로그인 성공
            String token=FirebaseInstanceId.getInstance().getToken();
            Map<String, Object> map=new HashMap<>();
            map.put("tokenId", token);
            FirebaseDatabase database=FirebaseDatabase.getInstance();
            database.getReference("app_users")
                    .child(user.getUid())
                    .updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(LOG_TAG, "onSuccess");

                            Toast.makeText(LoginActivity.this, user.getEmail()+"님 환영합니다.",
                                    Toast.LENGTH_SHORT).show();
                            //Intent intent=new Intent(this, PlayRTCActivity.class);

                            Intent intent=new Intent(getApplicationContext(), StartActivity.class);
                            intent.putExtra("uid", userInfo.getUid());
                            intent.putExtra("userName", userInfo.getUserName());
                            intent.putExtra("location", userInfo.getLocation());
                            startActivityForResult(intent, 100);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(LOG_TAG, "onFailure");
                        }
                    });


        }else{
            // 로그인 실패
            Toast.makeText(LoginActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //checkAppUser(currentUser.getEmail());
       // updateUI(currentUser);
    }


}

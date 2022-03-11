package com.example.chatfreeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatfreeapp.databinding.ActivityMain2Binding;
import com.example.chatfreeapp.databinding.ActivityMain3Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

public class MainActivity3 extends AppCompatActivity {

    ActivityMain3Binding binding;
    String fotp,userotp;
    OtpView otpView;
    private Button btnverify;
   private FirebaseAuth auth;
     private String VerificationId;
    private EditText mEditText;
    ProgressDialog dialog;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         binding = ActivityMain3Binding.inflate(getLayoutInflater());
         setContentView(R.layout.activity_main4);
         setContentView(binding.getRoot());
         dialog=new ProgressDialog(this);
         dialog.setMessage("Sending OTP...");
         dialog.setCancelable(false);
         dialog.show();;
         btnverify = findViewById(R.id.button2);
         auth = FirebaseAuth.getInstance();
         otpView = findViewById(R.id.otp_view);

                 String PhoneNumber = getIntent().getStringExtra("PhoneNumber");

         binding.textView.setText("Verify  " + PhoneNumber);
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(PhoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(MainActivity3.this)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                            binding.otpView.requestFocus();
                            super.onCodeSent(s, forceResendingToken);
                            dialog.dismiss();
                            VerificationId = s;

                        }

                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {

                        }

                    }).build();
            PhoneAuthProvider.verifyPhoneNumber(options);
            binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
                @Override
                public void onOtpCompleted(String otp) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, otp);
                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity3.this,Userprofile.class);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                Toast.makeText(MainActivity3.this, "Login Uncessfull", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }

    }

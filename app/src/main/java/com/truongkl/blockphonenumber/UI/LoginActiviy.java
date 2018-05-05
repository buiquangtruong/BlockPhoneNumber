package com.truongkl.blockphonenumber.UI;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.truongkl.blockphonenumber.Models.User;
import com.truongkl.blockphonenumber.R;
import com.truongkl.blockphonenumber.RoomDatabase.NumberDatabase;
import com.truongkl.blockphonenumber.RoomDatabase.UserDAO;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Truong KL on 11/18/2017.
 */

public class LoginActiviy extends AppCompatActivity {
    private static final String TAG = "LoginActivity" ;
    @BindView(R.id.edt_phone_number)
    EditText edtPhoneNumber;
    @BindView(R.id.edt_code)
    EditText edtCode;
    @BindView(R.id.txt_note)
    TextView txtNote;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private NumberDatabase numberDatabase;
    private UserDAO userDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        numberDatabase = NumberDatabase.getInstance(getApplicationContext());
        userDAO = numberDatabase.userDAO();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!= null){
            startActivity(new Intent(LoginActiviy.this, ListGroupActivity.class));
            finish();
        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    edtPhoneNumber.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(LoginActiviy.this, "Quota exceeded.", Toast.LENGTH_SHORT).show();
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                edtCode.setVisibility(View.VISIBLE);
                txtNote.setVisibility(View.VISIBLE);


                // ...
            }
        };

    }

    @OnClick(R.id.btn_login)
    public void login(){
        if (edtCode.getVisibility() == View.GONE){
            String number = edtPhoneNumber.getText().toString();
            if (!number.isEmpty()){
                startPhoneNumberVerification(number);
            }
        }else if (edtCode.getVisibility() == View.VISIBLE){
            String code = edtCode.getText().toString();
            if (!code.isEmpty()){
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        }

    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

       // mVerificationInProgress = true;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            UserInsert userInsert = new UserInsert();
                            userInsert.execute(new User(user.getPhoneNumber()));

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(LoginActiviy.this, "The verification code entered was invalid!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private class UserInsert extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... users) {
            userDAO.insertUser(users);
            return users[0];
        }

        @Override
        protected void onPostExecute(User user) {
            startActivity(new Intent(LoginActiviy.this, ListGroupActivity.class));
            finish();
        }
    }

}

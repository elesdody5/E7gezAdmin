package example.com.e7gezadmain.add_field;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.e7gezadmain.Enum;
import example.com.e7gezadmain.MainActivity;
import example.com.e7gezadmain.R;
import example.com.e7gezadmain.firebase.AppDatabase;
import example.com.e7gezadmain.firebase.collection.Stadium;

import static example.com.e7gezadmain.Enum.MY_PREFS_NAME;
import static example.com.e7gezadmain.Enum.NAME;
import static example.com.e7gezadmain.Enum.PHONE;
import static example.com.e7gezadmain.add_field.FieldActivity.notAvailable;

public class OwnerActivity extends AppCompatActivity {
    private static final String TAG = OwnerActivity.class.getName();
    private FirebaseAuth mAuth;
    @BindView(R.id.name)
    TextInputEditText name;

    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.code)
    TextInputEditText code;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.password_layout)
    TextInputLayout pass_layout;
    // to know is user or owner

    @BindView(R.id.send)
    MaterialButton send;
    @BindView(R.id.verify)
    MaterialButton verify_btn;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    Stadium stadium;
    String government;
    String city;
    String area;
    ArrayList<String> morning;
    ArrayList<String> night;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        ButterKnife.bind(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        stadium = (Stadium) getIntent().getSerializableExtra(Enum.STADIUM.toString());
        government = getIntent().getStringExtra("gov");
        city = getIntent().getStringExtra("city");
        area = getIntent().getStringExtra("area");
        morning = getIntent().getStringArrayListExtra("morning");
        night = getIntent().getStringArrayListExtra("night");
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                //  signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // Toast.makeText(getContext(), getContext().getResources().getString(R.string.wrong), Toast.LENGTH_LONG).show();
//
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                    // ...
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                    // ...
//                }

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
                // The corresponding whitelisted code above should be used to complete sign-in.
                Toast.makeText(OwnerActivity.this, "Please wait", Toast.LENGTH_SHORT).show();

                // ...
            }
        };
        mAuth.useAppLanguage();
        if (savedInstanceState != null)
            RestoreInstance(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME.toString(), name.getText().toString());
        outState.putString(PHONE.toString(), phone.getText().toString());
        outState.putInt("passVisible", pass_layout.getVisibility());
        //outState.putBoolean("is_user", isUser);
        outState.putString("password", password.getText().toString());

        outState.putString("code", code.getText().toString());
        outState.putString("send", send.getText().toString());
        if (mResendToken != null && mVerificationId != null) {
            outState.putString("id", mVerificationId);
            outState.putString("token", mResendToken.toString());
        }
        outState.putBoolean("verify_btn", verify_btn.isEnabled());
    }

    private void RestoreInstance(Bundle savedInstanceState) {
        name.setText(savedInstanceState.getString(NAME.toString()));
        phone.setText(savedInstanceState.getString(PHONE.toString()));
        password.setText(savedInstanceState.getString("password"));
        pass_layout.setVisibility(savedInstanceState.getInt("passVisible"));
        // isUser = savedInstanceState.getBoolean("is_user");
        mVerificationId = savedInstanceState.getString("id");
        // mResendToken = (PhoneAuthProvider.ForceResendingToken) savedInstanceState.get("token");
        code.setText(savedInstanceState.getString("code"));
        // send.setText(savedInstanceState.getString("send"));
        verify_btn.setEnabled(savedInstanceState.getBoolean("verify_btn"));
    }

    @OnClick(R.id.send)
    public void checkValidation() {
        if (name.getText().toString().isEmpty()) {
            name.setError(getResources().getString(R.string.name_error));
            return;
        }
        boolean PhoneValidation = checkPhone(phone.getText().toString());
        if (!PhoneValidation) {
            phone.setError(getResources().getString(R.string.phone_error));
            return;
        }


        // if (send.getText().equals(getResources().getString(R.string.send)))
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2" + phone.getText().toString(),        // Phone number to verify_btn
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);// OnVerificationStateChangedCallbacks

        verify_btn.setEnabled(true);
        //send.setText(getResources().getString(R.string.resend));
        // verify_btn.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));


    }

    private boolean checkPhone(String phone) {
        String prefix = phone.substring(0, 2);
        if (!prefix.equals("01"))
            return false;
        // where regexStr can be:
        //matches 10-digit numbers only
        String regexStr = "^[0-9]{11}$";
        return phone.matches(regexStr);

    }

    @OnClick(R.id.verify)
    public void verify() {
        if (!code.getText().toString().isEmpty() && mVerificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code.getText().toString());
            signInWithPhoneAuthCredential(credential);
        } else
            code.setError(getResources().getString(R.string.no_empty));
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();

                        LoginOwner(user.getUid());

                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(OwnerActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void LoginOwner(String userId) {
        DocumentReference sDocumentReference = AppDatabase.getStadiumCollection().document();
        addOwnerDetails(userId);
        addStadiumDetails(userId,sDocumentReference);
        addLocationDetails(sDocumentReference);
        Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void addLocationDetails(DocumentReference sDocumentReference) {
        DocumentReference goveDocumentReference = AppDatabase.getGovernmentsCollection().document(government);
        // set any data in document to make it appear in server
        goveDocumentReference.set(new HashMap<String,String>(){{put("name",government);}});
        DocumentReference cityDocumentReference = goveDocumentReference.collection("cities").document(city);
        cityDocumentReference.set(new HashMap<String,String>(){{put("name",city);}});

        DocumentReference areaDocumentReference= cityDocumentReference
                .collection("areas")
                .document(area);
        areaDocumentReference.set(new HashMap<String,String>(){{put("name",area);}});

        areaDocumentReference.collection("stadiums").document(sDocumentReference.getId()).set(new HashMap<String,DocumentReference>(){{put("stadium",sDocumentReference);}});

    }

    private void addStadiumDetails(String userId,DocumentReference sDocumentReference) {
        Map<String, Object> stadiumData = new HashMap<>();
        stadiumData.put("name", stadium.getName());
        stadiumData.put("description", stadium.getDescription());
        stadiumData.put("address", stadium.getAddress());
        stadiumData.put("morning", morning);
        stadiumData.put("night", night);
        stadiumData.put("owner",AppDatabase.getOwnerCollection().document(userId));
        if(!notAvailable.isEmpty())
            stadiumData.put("not_available",notAvailable);
        stadiumData.put("price",stadium.getPrice());
        sDocumentReference.set(stadiumData, SetOptions.merge());

        // add stadiumDetails to owner document
        Map<String, Object> stringDocumentReferenceMap = new HashMap<>();
        stringDocumentReferenceMap.put("stadium", sDocumentReference);
        // add name to make it easy to get name at main application in owner activity
        stringDocumentReferenceMap.put("name", stadium.getName());

        AppDatabase.getOwnerCollection().document(userId).collection("stadiums").
                document(sDocumentReference.getId()).set(stringDocumentReferenceMap);


    }

    private void addOwnerDetails(String userId) {
        Map<String, String> Ownerdata = new HashMap<>();
        Ownerdata.put("name", name.getText().toString());
        Ownerdata.put("phone", phone.getText().toString());
        Ownerdata.put("password",password.getText().toString());
        AppDatabase.getOwnerCollection().document(userId).set(Ownerdata,SetOptions.merge());
    }
}

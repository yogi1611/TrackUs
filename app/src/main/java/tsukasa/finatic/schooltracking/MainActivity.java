package tsukasa.finatic.schooltracking;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;
import tsukasa.finatic.schooltracking.Model.User;
import tsukasa.finatic.schooltracking.utils.Common;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    //database reference
    DatabaseReference user_information;
    private static final int MY_REQUEST_CODE=2108;
    List<AuthUI.IdpConfig> providers;

    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        Paper.init(this);

        //Initialising Firebase
        user_information = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);

        //Initialising Providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );


        //Request permission of location with dexter
        Dexter.withContext( getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(MainActivity.this," Permission to use app is granted",Toast.LENGTH_LONG).show();


                        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                                new ActivityResultCallback<ActivityResult>() {
                                    @Override
                                    public void onActivityResult(ActivityResult result) {
                                        if(result.getResultCode() == Activity.RESULT_OK){
                                            Intent intent = result.getData();
                                            // Successfully signed in
                                            //result ok is -1
                                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            //check if user exists in database
                                            user_information.orderByKey()
                                                    .equalTo(firebaseUser.getUid())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.getValue() == null) { //if user is not exists
                                                                if (!snapshot.child(firebaseUser.getUid()).exists()) //if key uid is not exists
                                                                {
                                                                    Common.loggedUser = new User(firebaseUser.getUid(), firebaseUser.getEmail(),firebaseUser.getPhotoUrl());
                                                                    //Adding to database
                                                                    user_information.child(Common.loggedUser.getUid())
                                                                            .setValue(Common.loggedUser);
                                                                }
                                                            } else { //if user is available
                                                                Common.loggedUser = snapshot.child(firebaseUser.getUid()).getValue(User.class);

                                                            }

                                                            // Saving UID to storage to update location from background
                                                            Paper.book().write(Common.USER_UID_SAVE_KEY, Common.loggedUser.getUid());

                                                            final DatabaseReference tokens = FirebaseDatabase.getInstance()
                                                                    .getReference(Common.TOKENS);

                                                            //getting token
                                                            //since FirebaseInstanceId is depreciated
                                                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                                                                if (!TextUtils.isEmpty(token)) {
                                                                    tokens.child(firebaseUser.getUid())
                                                                            .setValue(token);
                                                                } else {
                                                                    Log.w("error", "token should not be null...");
                                                                }
                                                            });

                                                            startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                                            finish();




                                                            //Now here Navigating to home
                                                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                            finish();

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }

                                                        ;




                                                    });


                                        };
                                    }});



                        launcher.launch(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(providers)
                                .build());

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this,"You must accept Permission to use app",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();



    }
}






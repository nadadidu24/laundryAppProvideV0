package my.laundryapp.laundryappproviderv0;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import my.laundryapp.laundryappproviderv0.common.Common;
import my.laundryapp.laundryappproviderv0.model.ServerUserModel;

public class MainActivity extends AppCompatActivity {

    private static int APP_REQUEST_CODE = 7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog1;
    private DatabaseReference serverRef;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if(listener !=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());

        serverRef = FirebaseDatabase.getInstance().getReference(Common.SERVER_REF); //mcm kena tukar
        firebaseAuth = FirebaseAuth.getInstance();
        dialog1 = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        listener = firebaseAuthLocal ->{
            //check ada luser log in or not
            FirebaseUser user = firebaseAuthLocal.getCurrentUser();
            if(user != null)
            {
                //check user from firebase
                checkServerUserFromFirebase(user);

            }
            else
            {
                phoneLogin();

            }


        };

    }

    private void checkServerUserFromFirebase(FirebaseUser user) {
        dialog1.show();
        serverRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            ServerUserModel userModel = snapshot.getValue(ServerUserModel.class);

                            if(userModel.isActive())
                            {
                              goToHomeActivity(userModel);
                            }
                            else
                            {

                                dialog1.dismiss();
                                Toast.makeText(MainActivity.this, "You must be allowed from Admin to access this app", Toast.LENGTH_SHORT).show();
                            }


                        }
                        else
                        {
                            //user not exist in database
                            dialog1.dismiss();
                            showRegisterDialog(user);
                           
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog1.dismiss();
                        Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRegisterDialog(FirebaseUser user) {


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage("Please Fill infromation . \nAdmin will accept sooner");

        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register,null);
        EditText edt_name = (EditText) itemView.findViewById(R.id.edt_name);
        EditText edt_phone = (EditText) itemView.findViewById(R.id.edt_phone);




        //set data
        edt_phone.setText(user.getPhoneNumber());
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(TextUtils.isEmpty(edt_name.getText().toString()))
                        {
                            Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ServerUserModel serverUserModel = new ServerUserModel();
                        serverUserModel.setUid(user.getUid());
                        serverUserModel.setName(edt_name.getText().toString());
                        serverUserModel.setPhone(edt_phone.getText().toString());
                        serverUserModel.setActive(false); //default failed,manually change in firebase
                        dialog1.show();

                        serverRef.child(serverUserModel.getUid())
                                .setValue(serverUserModel)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog1.dismiss();
                                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog1.dismiss();
                                Toast.makeText(MainActivity.this, "Congratulations! register success!", Toast.LENGTH_SHORT).show();
                                //goToHomeActivity(serverUserModel);
                            }
                        });

                    }
                });

        builder.setView(itemView);

        androidx.appcompat.app.AlertDialog resgisterDialog = builder.create();
        resgisterDialog.show();

    }

    private void goToHomeActivity(ServerUserModel serverUserModel) {

        dialog1.dismiss();
        Common.currentServerUser =serverUserModel;
        startActivity(new Intent(this,HomeActivity.class));
        finish();

    }

    private void phoneLogin() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == APP_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
            else
            {
                Toast.makeText(this,"Failed to sign in",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
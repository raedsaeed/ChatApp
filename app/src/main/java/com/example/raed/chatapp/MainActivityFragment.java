package com.example.raed.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "MainActivityFragment";
    public static final int RC_SIGN_IN = 1;
    public static final int RC_SEND_PHOTO = 2;
    public static final int MAX_MESSAGE_LENGTH = 1000;
    public static final String USER = "Anonymous";

    private RecyclerView recyclerView;
    private ImageButton sendImage;
    private EditText messageField;
    private Button sendButton;
    private MessageListAdapter adapter;
    private List<Message> messageList;
    private String userName;

    //Firebase Variables
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    //ChildEventListener is an interface that make it possible to retrieve data from database and display
    // it in RecyclerView
    private ChildEventListener eventListener;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private Query query;
    private ValueEventListener valueListener;


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu: called in fragments");
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        if(item.getItemId() == R.id.menu_singout) {
            Log.d(TAG, "onOptionsItemSelected: FragmentActivity sign out ");
            AuthUI.getInstance().signOut(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        userName = USER;
        recyclerView = view.findViewById(R.id.messages_list);
        sendButton = view.findViewById(R.id.send_button);
        messageField = view.findViewById(R.id.message_field);
        sendImage = view.findViewById(R.id.send_image);

        sendButton.setOnClickListener(this);
        sendImage.setOnClickListener(this);

        messageField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_MESSAGE_LENGTH)});
        messageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() > 0){
                    sendButton.setEnabled(true);
                    sendImage.setVisibility(View.GONE);
                }else {
                    sendButton.setEnabled(false);
                    sendImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Initialize MessageAdapter then set RecyclerView adapter
        messageList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageListAdapter(messageList);
        recyclerView.setAdapter(adapter);

        //Initialize database and it's databaseReference
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("messages");

        //Initialize storage and it's storageReference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("messages_pics");

        // Initialize FirebaseAuth and AuthListener
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    onSignedInInitialized(user.getDisplayName());
                }else {
                    onSignedOutInitialized();
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(AuthUI.GOOGLE_PROVIDER, AuthUI.EMAIL_PROVIDER)
                            .build(), RC_SIGN_IN);
                }
            }
        };
        auth.addAuthStateListener(authListener);
        return view;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: onclick");
        int id = v.getId();
        switch (id) {
            case R.id.send_button:
                Message message = new Message(userName, messageField.getText().toString(), null);
                databaseReference.push().setValue(message);
                messageField.setText("");
                break;
            case R.id.send_image:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Choose an image !!"), RC_SEND_PHOTO);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SEND_PHOTO:
                Uri imageUri = data.getData();
                StorageReference reference = storageReference.child(imageUri.getLastPathSegment());
                UploadTask task = reference.putFile(imageUri);
                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Message message = new Message(USER, null, downloadUrl.toString());
                        databaseReference.push().setValue(message);
                        Log.d(TAG, "onSuccess: Send image successfully");
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure " + e.getMessage());
                    }
                });
                break;
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: Called");
        auth.addAuthStateListener(authListener);
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: Called");
        auth.removeAuthStateListener(authListener);
        detachEventListener();
        super.onPause();
    }

    /**
     * Initialize ChildEventListener and attach it DatabaseReference that make it possible for the data in databae
     * to be shown as a List using RecyclerView List
     */
    private void addChildEventListener () {
        if(eventListener == null ) {
            eventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Message message = dataSnapshot.getValue(Message.class);
                    recyclerView.smoothScrollToPosition(adapter.addItem(message));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        databaseReference.addChildEventListener(eventListener);
    }

    public void onSignedInInitialized (String user){
        userName = user;
        addChildEventListener();
    }

    private void detachEventListener() {
        if(eventListener != null) {
            databaseReference.removeEventListener(eventListener);
            eventListener = null;
        }
    }

    public void onSignedOutInitialized () {
        userName = USER;
        adapter.clearData();
        recyclerView.removeAllViews();
        detachEventListener();
    }
}





















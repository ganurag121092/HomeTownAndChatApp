package edu.sdsu.anuragg.hometownandchatapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserChatFragment extends Fragment {
    private ProgressDialog progressDialog;
    String chatRoomAB, chatRoomBA, currentUser, selectedUser, currentUid, selectedUid, message;
    EditText messageText;
    TextView recipientName;
    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddmmss",Locale.ENGLISH);
    RelativeLayout relativeLayout;
    private RecyclerView chatRecyclerView;
    private ArrayList<Chat> chatArrayList = new ArrayList<>();;
    private static ChatListAdapter chatListAdapter;
    LinearLayoutManager mLayoutManager;

    public UserChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
            getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    @Override
    public void onStop(){
        super.onStop();
        getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();

        View view = inflater.inflate(R.layout.fragment_user_chat, container, false);
        messageText = (EditText) view.findViewById(R.id.msgText);
        Button sendBtn = (Button) view.findViewById(R.id.sendID);

        currentUser = args.getString("currentUser");
        selectedUser = args.getString("selectedUser");
        currentUid = args.getString("currentUserUID");
        selectedUid = args.getString("selectedUserUID");
        Log.d(TAG, "current User: " + currentUser);
        Log.d(TAG, "selectedUser: " + selectedUser);
        Log.d(TAG, "currentUid: "+currentUid);
        Log.d(TAG, "selectedUid" + selectedUid);
        chatRoomAB = currentUid + "_" + selectedUid;
        chatRoomBA = selectedUid + "_" + currentUid;
        chatListAdapter = new ChatListAdapter(chatArrayList);
        recipientName = (TextView) view.findViewById(R.id.recipientID);
        recipientName.setText(selectedUser);
        chatRecyclerView = (RecyclerView) view.findViewById(R.id.chats_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
        getMessageFromFirebaseUser(chatRoomAB,chatRoomBA);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = messageText.getText().toString();
                Chat chat = new Chat();
                chat.sender = currentUser.toLowerCase();
                chat.receiver = selectedUser.toLowerCase();
                chat.senderUid = currentUid;
                chat.receiverUid = selectedUid;
                chat.message = message;
                chat.timestamp = new Date();
                if(chat.getMessage()!=null){
                    Log.d(TAG, "typed message: " + message);
                    //Toast.makeText(getActivity(), "Send Button pressed, Message is " + message, Toast.LENGTH_SHORT).show();
                    sendMessageToFirebaseUser(getActivity(),chat);
                    messageText.setText("");
                } else {
                    //Toast.makeText(getActivity(), "Send Button pressed, Message is Empty", Toast.LENGTH_SHORT).show();
                }

            }
        });


        // Inflate the layout for this fragment
        return view;
    }



    public void getMessageFromFirebaseUser(final String chatRoom1, final String chatRoom2) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child("chats")
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(chatRoom1)) {
                            Log.e(TAG, "getMessageFromFirebaseUser: " + chatRoom1 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("chats")
                                    .child(chatRoom1)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            //Chat chat = dataSnapshot.getValue(Chat.class);
                                            onGetChild(dataSnapshot);

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
                                            // Unable to get message.
                                        }
                                    });
                        } else if (dataSnapshot.hasChild(chatRoom2)) {
                            Log.e(TAG, "getMessageFromFirebaseUser: " + chatRoom2 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("chats")
                                    .child(chatRoom2)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            //Chat chat = dataSnapshot.getValue(Chat.class);
                                            onGetChild(dataSnapshot);
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
                                            // Unable to get message.
                                        }
                                    });
                        } else {
                            Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to get message
                    }
                });
    }

    private void onGetChild(DataSnapshot dataSnapshot){
        Chat chat = dataSnapshot.getValue(Chat.class);

        Log.i("Snapshot now",chat.getMessage());
        chatArrayList.add(chat);
        if(chatRecyclerView!=null){
            chatRecyclerView.setVisibility(View.VISIBLE);
        }

        Log.d("User List SIZE", String.valueOf(chatArrayList.size()));

        chatListAdapter.notifyDataSetChanged();
        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatRecyclerView.setAdapter(chatListAdapter);

    }

    public void sendMessageToFirebaseUser(final Context context, final Chat chat){
        chatRoomAB = currentUid + "_" + selectedUid;
        chatRoomBA = selectedUid + "_" + currentUid;
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child("chats")
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(chatRoomAB)) {
                            Log.e(TAG, "sendMessageToFirebaseUser: " + chatRoomAB + " exists");
                            databaseReference.child("chats")
                                    .child(chatRoomAB)
                                    .child(String.valueOf(dataFormat.format(chat.timestamp)))
                                    .setValue(chat);
                        } else if (dataSnapshot.hasChild(chatRoomBA)) {
                            Log.e(TAG, "sendMessageToFirebaseUser: " + chatRoomBA + " exists");
                            databaseReference.child("chats")
                                    .child(chatRoomBA)
                                    .child(String.valueOf(dataFormat.format(chat.timestamp)))
                                    .setValue(chat);
                        } else {
                            Log.e(TAG, "sendMessageToFirebaseUser: success");
                            databaseReference.child("chats")
                                    .child(chatRoomAB)
                                    .child(String.valueOf(dataFormat.format(chat.timestamp)))
                                    .setValue(chat);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to get message
                    }
                });
    }


}

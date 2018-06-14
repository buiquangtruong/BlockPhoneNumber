package com.truongkl.blockphonenumber.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.truongkl.blockphonenumber.Adapter.GroupAdapter;
import com.truongkl.blockphonenumber.Key;
import com.truongkl.blockphonenumber.Models.Group;
import com.truongkl.blockphonenumber.Models.Number;
import com.truongkl.blockphonenumber.Models.User;
import com.truongkl.blockphonenumber.R;
import com.truongkl.blockphonenumber.RoomDatabase.GroupDAO;
import com.truongkl.blockphonenumber.RoomDatabase.NumberDAO;
import com.truongkl.blockphonenumber.RoomDatabase.NumberDatabase;
import com.truongkl.blockphonenumber.RoomDatabase.UserDAO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.truongkl.blockphonenumber.Key.LIST_BLOCK_NUMBER;
import static com.truongkl.blockphonenumber.Key.REQUEST_CODE_PERMISSION;

/**
 * Created by Truong KL on 11/15/2017.
 */

public class ListGroupActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private static final String TAG ="ListGroupActivity";
    public static String userPhoneNumber ;
//    public static String userPhoneNumber = "0964449404" ;

    private ArrayList<Group> groups;
    private GroupAdapter adapter;
    @BindView(R.id.rcv_list_group)
    RecyclerView rcvListGroup;
    private DatabaseReference mDatabase;
    private NumberDatabase numberDatabase;
    private GroupDAO groupDAO;
    private UserDAO userDAO;
    private NumberDAO numberDAO;
    private SettingActivity settingFragment;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        checkReadPhoneStatePermission();
//        checkCallPhonePermission();
        //checkReceiveSmsPermission();
        requestPermissions();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        numberDatabase = NumberDatabase.getInstance(getApplicationContext());
        groupDAO = numberDatabase.groupDAO();
        userDAO = numberDatabase.userDAO();
        numberDAO = numberDatabase.numberDAO();

        UserInsert userInsert = new UserInsert();
        userInsert.execute(new User(userPhoneNumber));

        loadDataFromFirebase();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                loadDataFromFirebase();
//            }
//        }).start();
       // initView();
    }

    private void loadDataFromFirebase() {
        mDatabase.child(LIST_BLOCK_NUMBER).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final User user = new User(dataSnapshot.getKey());
                UserInsert2 userInsert2 = new UserInsert2();
                userInsert2.execute(user);
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
        });

    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userPhoneNumber = currentUser.getPhoneNumber();

        groups = new ArrayList<>();
        adapter = new GroupAdapter(this, groups);
        adapter.setOnItemClickListener(new GroupAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ListGroupActivity.this, ListNumberActivity.class);
                intent.putExtra(Key.GROUP_ID, groups.get(position).getId());
                intent.putExtra(Key.GROUP_NAME, groups.get(position).getName());
                startActivity(intent);
            }
        });

        adapter.setOnLongClickListener(new GroupAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(final int position) {
                AlertDialog dialog = new AlertDialog.Builder(ListGroupActivity.this)
                        .setCancelable(true)
                        .setMessage("Delete " + groups.get(position).getName() + " ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeOnFirebase(groups.get(position).getName());
                                GroupDelete groupDelete = new GroupDelete();
                                groupDelete.execute(groups.get(position));
                                groups.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(ListGroupActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });
        rcvListGroup.setAdapter(adapter);
        rcvListGroup.setLayoutManager(new LinearLayoutManager(this));

        GroupLoader groupLoader = new GroupLoader();
        groupLoader.execute();
    }

    @OnClick(R.id.btn_add_group)
    public void addGroup(){
        final Dialog dialog = new Dialog(ListGroupActivity.this);
        // khởi tạo dialog
        dialog.setContentView(R.layout.dialog_add_group);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // xét layout cho dialog
        final TextView txtInput = dialog.findViewById(R.id.txt_input);
        Button btnAdd = dialog.findViewById(R.id.btn_add);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = txtInput.getText().toString();
                input.trim();
                if (!input.isEmpty()){
                    Group group = new Group(input, 0, userPhoneNumber);
                    GroupInsert groupInsert = new GroupInsert();
                    groupInsert.execute(group);
                    dialog.cancel();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
        // hiển thị dialog
    }

    private void requestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Nếu chưa cấp quyền thì mới xin
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED
                    ){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG,
                Manifest.permission.SYSTEM_ALERT_WINDOW}, REQUEST_CODE_PERMISSION);
            }

            if (!Settings.canDrawOverlays(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                        && grantResults[1] != PackageManager.PERMISSION_GRANTED
                        && grantResults[2] != PackageManager.PERMISSION_GRANTED){
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            // Settings activity never returns proper value so instead check with following method
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                   // initializeView();
                } else { //Permission is not available
                    Toast.makeText(this,
                            "Draw over other app permission not available. Closing the application",
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadGroupFromFirebase(){
        mDatabase.child(Key.LIST_BLOCK_NUMBER).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    dataSnapshot.getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            groups.add(new Group(dataSnapshot.getKey(),(int) dataSnapshot.getChildrenCount(), userPhoneNumber));
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
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
        });
    }

    private class GroupLoader extends AsyncTask<Void, Void, List<Group>>{

        @Override
        protected List<Group> doInBackground(Void... voids) {
            return groupDAO.loadGroupsByUserPhoneNumber(userPhoneNumber);
        }

        @Override
        protected void onPostExecute(List<Group> gr) {
            super.onPostExecute(gr);
            groups.clear();
            groups.addAll(gr);
            adapter.notifyDataSetChanged();
        }
    }

    private class GroupInsert extends AsyncTask<Group, Void, Group>{

        @Override
        protected Group doInBackground(Group... groups) {
            try{
                groupDAO.insertGroup(groups);
            }catch (SQLiteConstraintException e){
                e.printStackTrace();
                return null;
            }
            return groups[0];
        }

        @Override
        protected void onPostExecute(Group group) {
            super.onPostExecute(group);
            if (group == null){
                Toast.makeText(ListGroupActivity.this, "This group is existed!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ListGroupActivity.this, "Added!", Toast.LENGTH_SHORT).show();
                GroupLoader groupLoader = new GroupLoader();
                groupLoader.execute();
            }

        }
    }

    private class GroupInsert2 extends AsyncTask<Group, Void, Group>{

        @Override
        protected Group doInBackground(Group... groups) {
            int groupId;
            try{
                groupId = (int) groupDAO.insert(groups[0]);
            }catch (SQLiteConstraintException e){
                return null;
            }
            return groupDAO.findGroupById(groupId);
        }

        @Override
        protected void onPostExecute(final Group group) {
            if (group != null){
                mDatabase.child(LIST_BLOCK_NUMBER).child(group.getUserPhoneNumber()).child(group.getName())
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Number number = new Number(dataSnapshot.getValue().toString(), group.getId());
                                NumberInsert numberInsert = new NumberInsert();
                                numberInsert.execute(number);
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
                        });
            }
        }
    }

    private class GroupDelete extends AsyncTask<Group, Void, Void>{

        @Override
        protected Void doInBackground(Group... groups) {
            groupDAO.deleteGroup(groups[0]);
            return null;
        }
    }

    private void removeOnFirebase(final String groupName) {
        mDatabase.child(Key.LIST_BLOCK_NUMBER).child(userPhoneNumber).child(groupName).removeValue();
    }

    private class UserInsert extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... users) {
            userDAO.insertUser(users);
            return users[0];
        }

        @Override
        protected void onPostExecute(User user) {
            initView();
        }
    }

    private class UserInsert2 extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... users) {
            userDAO.insertUser(users);
            return users[0];
        }

        @Override
        protected void onPostExecute(final User user) {
            mDatabase.child(LIST_BLOCK_NUMBER).child(user.getPhoneNumber()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    final Group group = new Group(dataSnapshot.getKey(), (int) dataSnapshot.getChildrenCount(), user.getPhoneNumber());
                    GroupInsert2 groupInsert2 = new GroupInsert2();
                    groupInsert2.execute(group);
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
            });
        }
    }

    private class NumberInsert extends AsyncTask<Number, Void, Exception>{

        @Override
        protected Exception doInBackground(Number... numbers) {
            try {
                numberDAO.insertNumber(numbers);
            }catch (SQLiteConstraintException e){
                Log.d(TAG, "doInBackground: " + e.toString());
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
        }
    }

    @Override
    protected void onResume() {
        GroupLoader groupLoader = new GroupLoader();
        groupLoader.execute();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActiviy.class));
                finish();
                return true;
            case R.id.setting:
                //TODO

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.img_setting)
    public void onButtonSettingClicked(){
        startActivity(new Intent(this, SettingActivity.class));
    }
}

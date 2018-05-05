package com.truongkl.blockphonenumber.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.truongkl.blockphonenumber.Adapter.BlockNumberAdapter;
import com.truongkl.blockphonenumber.Key;
import com.truongkl.blockphonenumber.Models.Group;
import com.truongkl.blockphonenumber.Models.Number;
import com.truongkl.blockphonenumber.R;
import com.truongkl.blockphonenumber.RoomDatabase.GroupDAO;
import com.truongkl.blockphonenumber.RoomDatabase.NumberDAO;
import com.truongkl.blockphonenumber.RoomDatabase.NumberDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Truong KL on 11/15/2017.
 */

public class ListNumberActivity extends AppCompatActivity {
    private static final String TAG = "ListNumberActivity" ;
    @BindView(R.id.rcv_list_number)
    RecyclerView rcvListNumber;
    private ArrayList<Number> numbers;
    private BlockNumberAdapter adapter;
    private DatabaseReference mDatabase;
    private NumberDatabase numberDatabase;
    private GroupDAO groupDAO;
    private NumberDAO numberDAO;
    private HistoryCallFragment historyCallFragment;

    private int groupId;
    private String groupName;
    private String numberAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_number);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null){
            groupId = intent.getIntExtra(Key.GROUP_ID, 0);
            Log.d("ListNumberActivity", "onCreate: " + groupId);
            groupName = intent.getStringExtra(Key.GROUP_NAME);
        }

        initView();
    }

    private void initView() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        numberDatabase = NumberDatabase.getInstance(getApplicationContext());
        groupDAO = numberDatabase.groupDAO();
        numberDAO = numberDatabase.numberDAO();

        numbers = new ArrayList<>();
        adapter = new BlockNumberAdapter(this, numbers);
        adapter.setOnItemClickListener(new BlockNumberAdapter.OnItemClickListener() {
            @Override
            public void onClick(final int position) {
                AlertDialog dialog = new AlertDialog.Builder(ListNumberActivity.this)
                        .setCancelable(true)
                        .setMessage("Unblock " + numbers.get(position).getNumber() + " ?")
                        .setPositiveButton("Unblock", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeOnFirebase(numbers.get(position).getNumber());
                                NumberDelete numberDelete = new NumberDelete();
                                numberDelete.execute(numbers.get(position));
                                GroupUpdate groupUpdate = new GroupUpdate();
                                groupUpdate.execute(-1);
                                Toast.makeText(ListNumberActivity.this, "Unblocked!", Toast.LENGTH_SHORT).show();
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

        rcvListNumber.setAdapter(adapter);
        rcvListNumber.setLayoutManager(new LinearLayoutManager(this));

        NumberLoader numberLoader = new NumberLoader();
        numberLoader.execute();

    }

    public void add(){
        final Dialog dialog = new Dialog(ListNumberActivity.this);
        // khởi tạo dialog
        dialog.setContentView(R.layout.dialog_add_number);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // xét layout cho dialog
        final TextView txtInput = dialog.findViewById(R.id.txt_input);
        Button btnBlock = dialog.findViewById(R.id.btn_block);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = txtInput.getText().toString();
                input.trim();
                input = input.replace(" ", "");
                if (!input.isEmpty()){
                    Number number = new Number(input, groupId);
                    NumberInsert numberInsert = new NumberInsert();
                    numberInsert.execute(number);
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

    public void addFromHistory(final String num){

        AlertDialog dialog = new AlertDialog.Builder(ListNumberActivity.this)
                .setCancelable(true)
                .setMessage("Do you want to block this number: " + num + " ?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Number number = new Number(num, groupId);
                        NumberInsert numberInsert = new NumberInsert();
                        numberInsert.execute(number);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        dialog.show();
    }

    @OnClick(R.id.fab_add_number)
    public void onFabClick(){
        final Dialog dialog = new Dialog(ListNumberActivity.this);
        // khởi tạo dialog
        dialog.setContentView(R.layout.dialog_type_add_number);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // xét layout cho dialog
        Button btnEnterNumber = dialog.findViewById(R.id.btn_enter_number);
        Button btnAddFromCallLog = dialog.findViewById(R.id.btn_add_from_call_log);

        btnEnterNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
                dialog.cancel();
            }
        });

        btnAddFromCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                if (historyCallFragment == null){
                    historyCallFragment = new HistoryCallFragment();
                }
                getFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, historyCallFragment )
                        .addToBackStack(null)
                        .commit();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private class NumberLoader extends AsyncTask<Void, Void, List<Number>> {

        @Override
        protected List<Number> doInBackground(Void... voids) {
            return numberDAO.loadNumbersByGroupId(groupId);
        }

        @Override
        protected void onPostExecute(List<Number> nums) {
            super.onPostExecute(nums);
            numbers.clear();
            numbers.addAll(nums);
            adapter.notifyDataSetChanged();
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
            numberAdd = numbers[0].getNumber();
            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            if (e == null){
                AlertDialog dialog = new AlertDialog.Builder(ListNumberActivity.this)
                        .setCancelable(true)
                        .setMessage("Do you want to share this numner?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pushUpFirebase(numberAdd);
                                dialog.cancel();
                                Toast.makeText(ListNumberActivity.this, "Blocked!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Toast.makeText(ListNumberActivity.this, "Blocked!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();
                dialog.show();

                GroupUpdate groupUpdate = new GroupUpdate();
                groupUpdate.execute(1);
                NumberLoader numberLoader = new NumberLoader();
                numberLoader.execute();
            }else{
                Toast.makeText(ListNumberActivity.this, "Số đã tồn tại!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class NumberDelete extends AsyncTask<Number, Void, Number>{

        @Override
        protected Number doInBackground(Number... numbers) {
            numberDAO.deleteNumber(numbers);
            return numbers[0];
        }

        @Override
        protected void onPostExecute(Number number) {
            numbers.remove(number);
            adapter.notifyDataSetChanged();
        }
    }

    private class GroupUpdate extends AsyncTask<Integer, Void, Void>{

        @Override
        protected Void doInBackground(Integer... integers) {
            Group group = groupDAO.findGroupById(groupId);
            if ((group.getCount() + integers[0]) < 0){
                groupDAO.updateCount(0, groupId);
            }else {
                groupDAO.updateCount(group.getCount() + integers[0], groupId);
            }

            return null;
        }
    }

    private void removeOnFirebase(final String number) {
        mDatabase.child(Key.LIST_BLOCK_NUMBER).child(ListGroupActivity.userPhoneNumber).child(groupName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.getValue().toString().equals(number)){
                        mDatabase.child(Key.LIST_BLOCK_NUMBER).child(ListGroupActivity.userPhoneNumber).child(groupName).child(dataSnapshot.getKey()).removeValue();
                    }
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

    private void pushUpFirebase(String number){
        mDatabase.child(Key.LIST_BLOCK_NUMBER).child(ListGroupActivity.userPhoneNumber)
                .child(groupName).push().setValue(number);
    }

    @OnClick(R.id.btn_back)
    public void back(){
        onBackPressed();
    }


}

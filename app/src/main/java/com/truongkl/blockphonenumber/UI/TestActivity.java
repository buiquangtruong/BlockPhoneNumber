package com.truongkl.blockphonenumber.UI;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BlockedNumberContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.truongkl.blockphonenumber.R;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Truong KL on 11/24/2017.
 */

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";

    @BindView(R.id.txt)
    TextView txt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ArrayList<String> listNumberInContacts = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Cursor c = getContentResolver().query(BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                    new String[]{BlockedNumberContract.BlockedNumbers.COLUMN_ID,
                            BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                            BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER}, null, null, null);

            c.moveToFirst();
            while (!c.isAfterLast()) {
                listNumberInContacts.add(c.getString(1));
                txt.setText(c.getString(1));
            }
            Log.d(TAG, "initView: " + listNumberInContacts);
        }
    }
}

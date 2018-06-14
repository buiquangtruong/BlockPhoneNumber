package com.truongkl.blockphonenumber.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.truongkl.blockphonenumber.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.truongkl.blockphonenumber.Key.FROM_HOUR;
import static com.truongkl.blockphonenumber.Key.FROM_MINUTE;
import static com.truongkl.blockphonenumber.Key.IS_AUTO_BLOCK;
import static com.truongkl.blockphonenumber.Key.IS_BLOCK_IN_PERIOD;
import static com.truongkl.blockphonenumber.Key.SAVE_TIME;
import static com.truongkl.blockphonenumber.Key.TO_HOUR;
import static com.truongkl.blockphonenumber.Key.TO_MINUTE;

/**
 * Created by Truong KL on 12/6/2017.
 */

public class SettingActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @BindView(R.id.txt_from)
    TextView txtFrom;
    @BindView(R.id.txt_to)
    TextView txtTo;
    @BindView(R.id.ln_time)
    LinearLayout lnTime;
    @BindView(R.id.rb_auto_block)
    RadioButton rbAutoBlock;
    @BindView(R.id.rb_block_in_period)
    RadioButton rbBlockInPeriod;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        lnTime.setVisibility(View.GONE);

        sharedPref = getSharedPreferences(SAVE_TIME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        int fromHour = sharedPref.getInt(FROM_HOUR, 8);
        int fromMinute = sharedPref.getInt(FROM_MINUTE, 0);
        int toHour = sharedPref.getInt(TO_HOUR, 18);
        int toMinute = sharedPref.getInt(TO_MINUTE, 0);

        txtFrom.setText("From " + fromHour + " : " + fromMinute );
        txtTo.setText("To " + toHour + " : " + toMinute );

        rbAutoBlock.setChecked(sharedPref.getBoolean(IS_AUTO_BLOCK, true));
        rbBlockInPeriod.setChecked(sharedPref.getBoolean(IS_BLOCK_IN_PERIOD, false));
        if (sharedPref.getBoolean(IS_BLOCK_IN_PERIOD, false)){
            lnTime.setVisibility(View.VISIBLE);
        }

        rbAutoBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    editor.putBoolean(IS_AUTO_BLOCK, true);
                    editor.putBoolean(IS_BLOCK_IN_PERIOD, false);
                    editor.commit();
                }
            }
        });

        rbBlockInPeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    lnTime.setVisibility(View.VISIBLE);
                    editor.putBoolean(IS_AUTO_BLOCK, false);
                    editor.putBoolean(IS_BLOCK_IN_PERIOD, true);
                    editor.commit();
                }else{
                    lnTime.setVisibility(View.GONE);
                }
            }
        });

        txtFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "timePicler");
                timePickerFragment.setOnSetDoneListener(new TimePickerFragment.OnSetDoneListener() {
                    @Override
                    public void onSetDone(int hourOfDay, int minute) {
                        editor.putInt(FROM_HOUR, hourOfDay);
                        editor.putInt(FROM_MINUTE, minute);
                        editor.commit();
                        txtFrom.setText(hourOfDay + " : " + minute);
                    }
                });
            }
        });

        txtTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "timePicler");
                timePickerFragment.setOnSetDoneListener(new TimePickerFragment.OnSetDoneListener() {
                    @Override
                    public void onSetDone(int hourOfDay, int minute) {
                        editor.putInt(TO_HOUR, hourOfDay);
                        editor.putInt(TO_MINUTE, minute);
                        editor.commit();
                        txtTo.setText(hourOfDay + " : " + minute);
                    }
                });
            }
        });
    }

    @OnClick(R.id.img_back)
    public void back(){
        onBackPressed();
    }

    @OnClick(R.id.btn_log_out)
    public void logOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActiviy.class);
        startActivity(intent);
        finishAffinity();
    }
}

package com.truongkl.blockphonenumber.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;
import com.truongkl.blockphonenumber.R;
import com.truongkl.blockphonenumber.UI.MyViewGroup;

import static com.truongkl.blockphonenumber.Key.BLOCK_NUMBER;
import static com.truongkl.blockphonenumber.Key.GROUP_NAME;

/**
 * Created by Truong KL on 12/1/2017.
 */

public class WarningCallService extends Service {
    private WindowManager windowManager;
    private MyViewGroup myViewGroup;
    private View subView;
    private WindowManager.LayoutParams mParams;
    private ImageView btnEnd;
    private TextView txtPhoneNumber;
    private TextView txtName;
    private RippleBackground rbAccount;
    private String number;
    private String groupName;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void initView() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        myViewGroup = new MyViewGroup(this);
        LayoutInflater minflater = LayoutInflater.from(this);
        subView = minflater.inflate(R.layout.activity_warn, myViewGroup);// nhet cai main vao cai viewGroup, de anh xa ra subView

        //dinh nghia param
        mParams = new WindowManager.LayoutParams();
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.CENTER;
        mParams.format = PixelFormat.TRANSLUCENT;//trong suot
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;// noi tren all be mat
        mParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// khong bi gioi han boi man hinh|Su duoc nut home
        windowManager.addView(myViewGroup,mParams);

        rbAccount = subView.findViewById(R.id.rb_account);
        rbAccount.startRippleAnimation();
        txtPhoneNumber = subView.findViewById(R.id.txt_phone_number);
        txtPhoneNumber.setText(number);
        txtName = subView.findViewById(R.id.txt_name);
        txtName.setText(groupName);
        btnEnd = subView.findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
              //  removieView();
            }
        });

    }

    private void removieView(){
        windowManager.removeView(subView);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            number = intent.getStringExtra(BLOCK_NUMBER);
            groupName = intent.getStringExtra(GROUP_NAME);
        }
        initView();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        removieView();
        super.onDestroy();
    }
}

package com.truongkl.blockphonenumber.UI;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.truongkl.blockphonenumber.R;

/**
 * Created by Truong KL on 6/18/2017.
 */

public class AddNumberDialog extends Dialog {
    EditText edtInput;
    Button btnCancel;

    public AddNumberDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDialog();
    }

    private void setupDialog(){
        requestWindowFeature(Window.FEATURE_NO_TITLE); //phải để trước setContentView
        setContentView(R.layout.dialog_add_number);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

//        ((MainActivity) getOwnerActivity()).test();
    }
}

package com.truongkl.blockphonenumber.UI;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.truongkl.blockphonenumber.Adapter.CallAdapter;
import com.truongkl.blockphonenumber.Key;
import com.truongkl.blockphonenumber.Models.Call;
import com.truongkl.blockphonenumber.R;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Truong KL on 11/29/2017.
 */

public class HistoryCallFragment extends Fragment {

    @BindView(R.id.rv_call_history)
    RecyclerView rvCallHistory;
    Unbinder unbinder;

    private ArrayList<Call> calls;
    private CallAdapter adapter;
    private ArrayList<Call> callsAdd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_call, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    private void initView() {
        calls = new ArrayList<>();
        callsAdd = new ArrayList<>();

        Cursor managedCursor = getActivity().managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        managedCursor.moveToFirst();
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            int callDuration = managedCursor.getInt(duration);
            int dircode = Integer.parseInt(callType);

            if (calls.size() > 0 && phNumber.equals(calls.get(0).getPhoneNumber())){
                calls.get(0).setCount(calls.get(0).getCount() +1 );
            }else{
                Call call = new Call(phNumber, dircode, callDayTime, callDuration);
                calls.add(0, call);
            }

        }

        adapter = new CallAdapter(calls);
        adapter.setOnItemClickListener(new CallAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                ((ListNumberActivity)getActivity()).addFromHistory(calls.get(position).getPhoneNumber());
            }
        });
        rvCallHistory.setAdapter(adapter);
        rvCallHistory.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @OnClick(R.id.btn_back)
    public void onBack(){
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

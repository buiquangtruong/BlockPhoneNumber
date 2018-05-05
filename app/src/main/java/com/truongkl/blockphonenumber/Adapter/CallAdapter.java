package com.truongkl.blockphonenumber.Adapter;

import android.graphics.Color;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.truongkl.blockphonenumber.Models.Call;
import com.truongkl.blockphonenumber.R;

import java.text.DateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Truong KL on 11/29/2017.
 */

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private ArrayList<Call> calls;

    public CallAdapter(ArrayList<Call> calls) {
        this.calls = calls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call, parent, false);
        final ViewHolder vh = new ViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClicked(vh.getLayoutPosition());
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(calls.get(position));
    }

    @Override
    public int getItemCount() {
        return calls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_phone_number)
        TextView txtPhoneNumber;
        @BindView(R.id.img_call_type)
        ImageView imgCallType;
        @BindView(R.id.txt_call_date)
        TextView txtCallDate;
        @BindView(R.id.txt_duration)
        TextView txtDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Call call){
            if (call.getCount() > 1){
                txtPhoneNumber.setText(call.getPhoneNumber() + "(" + call.getCount() + ")");
            }else {
                txtPhoneNumber.setText(call.getPhoneNumber());
            }

            txtCallDate.setText(DateFormat.getDateInstance().format(call.getCallDate()));
            txtDuration.setText(call.getDuration() + " seconds");
            switch (call.getCallType()){
                case CallLog.Calls.OUTGOING_TYPE:
                    imgCallType.setImageResource(R.drawable.ic_call_made_black_24dp);
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    imgCallType.setImageResource(R.drawable.ic_call_received_black_24dp);
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    imgCallType.setImageResource(R.drawable.ic_call_missed_black_24dp);
                    txtPhoneNumber.setTextColor(Color.parseColor("#F44336"));
                    break;
                default:
                    break;
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClicked(int position);
    }
}

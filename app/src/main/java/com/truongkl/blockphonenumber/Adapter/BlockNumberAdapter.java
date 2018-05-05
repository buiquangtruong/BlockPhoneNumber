package com.truongkl.blockphonenumber.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.truongkl.blockphonenumber.Models.Number;
import com.truongkl.blockphonenumber.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Truong KL on 11/8/2017.
 */

public class BlockNumberAdapter extends RecyclerView.Adapter<BlockNumberAdapter.ViewHolder> {
    private ArrayList<Number> numbers;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public BlockNumberAdapter(Context context, ArrayList<Number> numbers) {
        this.context = context;
        this.numbers = numbers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block_number, parent, false);
        final ViewHolder vh = new ViewHolder(itemView);
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onItemClickListener != null) {
//                    onItemClickListener.onClick(vh.getLayoutPosition());
//                }
//            }
//        });
        itemView.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(vh.getLayoutPosition());
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(numbers.get(position));
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_number)
        TextView txtNumber;
        @BindView(R.id.btn_delete)
        ImageView btnDelete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bind(final Number number){
            txtNumber.setText(number.getNumber());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onClick(int position);

    }
}

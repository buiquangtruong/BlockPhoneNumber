package com.truongkl.blockphonenumber.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.truongkl.blockphonenumber.Models.Group;
import com.truongkl.blockphonenumber.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Truong KL on 11/8/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private ArrayList<Group> groups;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnLongClickListener onLongClickListener;

    public GroupAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        final ViewHolder vh = new ViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(vh.getLayoutPosition());
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onLongClickListener != null){
                    onLongClickListener.onLongClick(vh.getLayoutPosition());
                }
                return false;
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_group_name)
        TextView txtGroupName;
        @BindView(R.id.txt_number)
        TextView txtNumber;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bind(final Group group){
            txtGroupName.setText(group.getName());
            if (group.getCount()>= 2){
                txtNumber.setText(group.getCount() + " numbers");
            }else{
                txtNumber.setText(group.getCount() + " number");
            }

        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public interface OnItemClickListener {

        void onClick(int position);

    }

    public interface OnLongClickListener{
        void onLongClick(int position);
    }
}

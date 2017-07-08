package com.ninad.ninhydrin.mailbox.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninad.ninhydrin.mailbox.Model;
import com.ninad.ninhydrin.mailbox.R;

import java.util.ArrayList;


public class ReadRecyclerAdapter extends RecyclerView.Adapter<ReadViewHolder> {

    public interface ItemClickListener{
        void itemClicked(int pos, Model.Letter letter);
    }

    private ItemClickListener listener;

    public ReadRecyclerAdapter(ItemClickListener listener, Context context, ArrayList<Model.Letter> letters) {
        this.listener = listener;
        this.context = context;
        this.letters = letters;
    }

    private Context context;
    private ArrayList<Model.Letter> letters;


    @Override
    public ReadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false);
        return new ReadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReadViewHolder holder, int position) {
        holder.Name.setText(letters.get(position).getSenderName());
        holder.ID.setText(letters.get(position).getSenderID());
        holder.Date.setText(letters.get(position).getDate());
        holder.bind(position,listener,letters.get(position));
    }

    @Override
    public int getItemCount() {
        return letters.size();
    }
}

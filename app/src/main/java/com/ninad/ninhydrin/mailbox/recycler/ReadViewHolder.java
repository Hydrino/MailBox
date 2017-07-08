package com.ninad.ninhydrin.mailbox.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ninad.ninhydrin.mailbox.Model;
import com.ninad.ninhydrin.mailbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;


class ReadViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.read_sender_name)
    TextView Name;
    @BindView(R.id.read_sender_id)
    TextView ID;
    @BindView(R.id.read_date)
    TextView Date;


    ReadViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    void bind(final int position, final ReadRecyclerAdapter.ItemClickListener listener, final Model.Letter letter) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClicked(position, letter);
            }
        });
    }
}

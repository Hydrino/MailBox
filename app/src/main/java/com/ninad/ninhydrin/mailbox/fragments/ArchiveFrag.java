package com.ninad.ninhydrin.mailbox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ninad.ninhydrin.mailbox.MVP;
import com.ninad.ninhydrin.mailbox.Model;
import com.ninad.ninhydrin.mailbox.R;
import com.ninad.ninhydrin.mailbox.recycler.ReadRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArchiveFrag extends Fragment implements ReadRecyclerAdapter.ItemClickListener {

    private Context context;
    private String RecipientID;
    private MVP.ViewToPresenter viewToPresenter;
    private ReadRecyclerAdapter adapter;

    @BindView(R.id.read_recycler)
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        SharedPreferences God = context.getSharedPreferences(getString(R.string.god), Context.MODE_PRIVATE);
        RecipientID = God.getString(getString(R.string.user_id), "");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.read, container, false);
        ButterKnife.bind(this, view);

        // send request to get read letters
        if (RecipientID != null && !RecipientID.equals("") && viewToPresenter != null) {
            viewToPresenter.retrieveArchiveLetters(RecipientID);

        }


        return view;
    }

    public void setViewToPresenter(MVP.ViewToPresenter viewToPresenter) {
        this.viewToPresenter = viewToPresenter;
    }

    public void passLetters(ArrayList<Model.Letter> archiveLetters) {
        adapter = new ReadRecyclerAdapter(this, context, archiveLetters);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        viewToPresenter.ArchiveLetterShownSuccess();
    }

    @Override
    public void itemClicked(int pos, Model.Letter letter) {
        Bundle bundle = new Bundle();

        bundle.putString(getString(R.string.letter_date), letter.getDate());
        bundle.putString(getString(R.string.letter_name), letter.getSenderName());
        bundle.putString(getString(R.string.letter_body), letter.getBody());

        LetterFrag letterFrag = new LetterFrag();
        letterFrag.setArguments(bundle);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, letterFrag);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}

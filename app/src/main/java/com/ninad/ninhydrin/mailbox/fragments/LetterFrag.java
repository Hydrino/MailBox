package com.ninad.ninhydrin.mailbox.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninad.ninhydrin.mailbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LetterFrag extends Fragment {

    @BindView(R.id.letter_layout_body)
    TextView Body;
    @BindView(R.id.letter_layout_date)
    TextView Date;
    @BindView(R.id.letter_layout_name)
    TextView Name;

    private String name, date, body;


    public interface LetterFragListener {
        void FragAttached();

        void FragDetached();
    }

    private LetterFragListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            name = bundle.getString(getString(R.string.letter_name));
            date = bundle.getString(getString(R.string.letter_date));
            body = bundle.getString(getString(R.string.letter_body));
        }
    }

    @Override
    public void onDetach() {
        listener.FragDetached();
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (LetterFragListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        listener.FragAttached();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.letter_layout, container, false);
        ButterKnife.bind(this, view);
        Name.setText(name);
        Date.setText(date);
        Body.setText(body);
        return view;
    }
}

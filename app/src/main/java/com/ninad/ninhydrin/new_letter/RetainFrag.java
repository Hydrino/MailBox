package com.ninad.ninhydrin.new_letter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


public class RetainFrag extends Fragment {

    private Presenter presenter;

    public Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(Presenter presenter) {

        this.presenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}

package com.ninad.ninhydrin.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ninad.ninhydrin.mailbox.MainActivity;
import com.ninad.ninhydrin.mailbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity implements MVP.PresenterToView {

    private Presenter presenter;
    private MVP.ViewToPresenter viewToPresenter;
    private static final String FRAGMENT_TAG = "Fragment_Tag";
    @BindView(R.id.sign_up_name)
    EditText Name;
    @BindView(R.id.sign_up_id)
    EditText ID;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.sign_up_button)
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        ButterKnife.bind(this);

        initPresenter();
    }

    private void initPresenter() {
        FragmentManager manager = getSupportFragmentManager();
        RetainFrag retainFrag = (RetainFrag) manager.findFragmentByTag(FRAGMENT_TAG);

        if (retainFrag == null) {  // activity started for first time
            retainFrag = new RetainFrag();
            manager.beginTransaction().add(retainFrag, FRAGMENT_TAG).commit();

            //create new presenter and put into retainFrag
            presenter = new Presenter();
            retainFrag.setPresenter(presenter);
        } else {   // fragment retained
            Toast.makeText(this, "Retained!", Toast.LENGTH_SHORT).show();
            // get presenter from retained fragment
            presenter = retainFrag.getPresenter();
        }

        // pass presenterToView to Presenter
        presenter.attach(this);
        viewToPresenter = presenter.getViewToPresenter();

    }

    @OnClick(R.id.sign_up_button)
    public void onClick() {
        viewToPresenter.buttonClicked(Name.getText().toString(), ID.getText().toString());
    }


    @Override
    public void showSuccess(String Name, String ID) {
        SharedPreferences God = getSharedPreferences(getString(R.string.god), MODE_PRIVATE);
        SharedPreferences.Editor editor = God.edit();
        editor.putBoolean(getString(R.string.is_user_registered), true);
        editor.putString(getString(R.string.user_name), Name);
        editor.putString(getString(R.string.user_id), ID);
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showProgressBarAndDisableButton() {
        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);
    }

    @Override
    public void hideProgressBarAndEnableButton() {
        progressBar.setVisibility(View.GONE);
        button.setEnabled(true);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

}

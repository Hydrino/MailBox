package com.ninad.ninhydrin.new_letter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ninad.ninhydrin.mailbox.*;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewLetterActivity extends AppCompatActivity implements MVP.PresenterToView {

    @BindView(R.id.new_letter_toolbar)
    Toolbar toolbar;
    @BindView(R.id.new_letter_id)
    EditText ID;
    @BindView(R.id.new_letter_message)
    EditText Message;

    private Presenter presenter;
    private MVP.ViewToPresenter viewToPresenter;
    private static final String RETAIN_FRAG_TAG = "FragTag";
    private String SenderID;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.ic_check_black_24dp);

        drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.white), PorterDuff.Mode.SRC_IN);

        menu.add(Menu.NONE, 0, Menu.NONE, "Send Letter").setIcon(drawable).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_letter_layout);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initPresenter();

        SharedPreferences God = getSharedPreferences(getString(R.string.god), MODE_PRIVATE);
        SenderID = God.getString(getString(R.string.user_id), "");
    }

    private void initPresenter() {


        FragmentManager manager = getSupportFragmentManager();
        RetainFrag retainFrag = (RetainFrag) manager.findFragmentByTag(RETAIN_FRAG_TAG);

        if (retainFrag == null) {
            retainFrag = new RetainFrag();
            manager.beginTransaction().add(retainFrag, RETAIN_FRAG_TAG).commit();

            presenter = new Presenter();
            retainFrag.setPresenter(presenter);
        } else {
            presenter = retainFrag.getPresenter();
        }

        presenter.attach(this);

        viewToPresenter = presenter.getViewToPresenter();
    }

    @Override
    protected void onDestroy() {
        presenter.detach();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 0) {
            viewToPresenter.SendClicked(SenderID, ID.getText().toString(), Message.getText().toString());
            return true;
        }
        return false;
    }

    @Override
    public void showSuccess() {
        Toast.makeText(this, "The postman is delivering your letter!", Toast.LENGTH_SHORT).show();
        viewToPresenter.unSub();
        finish();
    }

    @Override
    public void showFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

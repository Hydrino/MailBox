package com.ninad.ninhydrin.mailbox;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ninad.ninhydrin.mailbox.fragments.ArchiveFrag;
import com.ninad.ninhydrin.mailbox.fragments.LetterFrag;
import com.ninad.ninhydrin.mailbox.fragments.ReadFrag;
import com.ninad.ninhydrin.mailbox.fragments.UnreadFrag;
import com.ninad.ninhydrin.new_letter.NewLetterActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MVP.PresenterToView, LetterFrag.LetterFragListener {

    @BindView(R.id.nav_drawer)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_frame)
    FrameLayout mainFrame;
    @BindView(R.id.nav_bar_items_list)
    ListView navBarList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.new_letter_fab)
    FloatingActionButton fab;

    private FragmentManager fragmentManager;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String[] NavBarItems = {"Unread", "Read", "Archive"};
    private int currentPagePosition;
    private MVP.ViewToPresenter viewToPresenter;
    private Presenter presenter;
    private static final String RETAIN_FRAG_TAG = "RetainFragTag";
    private final UnreadFrag unReadFrag = new UnreadFrag();
    private final ReadFrag readFrag = new ReadFrag();
    private final ArchiveFrag archiveFrag = new ArchiveFrag();
    private RetainFrag retainFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // initialise all the variables
        initVariables();

        // initialise the presenter and current page position
        // important to call it before setPageByPosition
        // because Fragments need the presenter
        // also this method should be called before init Action Bar
        //because it's title depends on current page position
        initPresenterAndCurrentPagePosition();

        //set the action bar and it's properties
        initActionBar();

        // set start page, default current page position is 0
        // however to retain current page in case of reconfiguration
        //this method is used
        setPageByPosition(currentPagePosition);

        // initialise nav drawer item list
        initList();

        // adding the drawer listener to nav drawer
        initNavDrawer();

    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(NavBarItems[currentPagePosition]);
    }

    private void initVariables() {
        fragmentManager = getSupportFragmentManager();
    }

    private void initPresenterAndCurrentPagePosition() {
        retainFrag = (RetainFrag) fragmentManager.findFragmentByTag(RETAIN_FRAG_TAG);

        if (retainFrag == null) {
            retainFrag = new RetainFrag();
            fragmentManager.beginTransaction().add(retainFrag, RETAIN_FRAG_TAG).commit();
            presenter = new Presenter();
            currentPagePosition = 0;

            retainFrag.setPresenter(presenter);
            retainFrag.setCurrentPagePosition(currentPagePosition);
        } else {
            presenter = retainFrag.getPresenter();
            currentPagePosition = retainFrag.getCurrentPagePosition();
            Log.w("new", "Retained");

            // don't know why sometimes presenter returned is null
            // typically when returning from NewLetterActivity
            if (presenter == null) {
                presenter = new Presenter();
                retainFrag.setPresenter(presenter);
                Log.w("new", "null presenter");
            }

        }

        presenter.attach(this);
        viewToPresenter = presenter.getViewToPresenter();
    }

    private void setPageByPosition(int currentPagePosition) {
        switch (currentPagePosition) {
            case 0:
                fragmentManager.popBackStack();
                unReadFrag.setViewToPresenter(viewToPresenter);
                fragmentManager.beginTransaction().replace(R.id.main_frame, unReadFrag).commit();
                break;
            case 1:
                fragmentManager.popBackStack();
                readFrag.setViewToPresenter(viewToPresenter);
                fragmentManager.beginTransaction().replace(R.id.main_frame, readFrag).commit();
                break;
            case 2:
                fragmentManager.popBackStack();
                archiveFrag.setViewToPresenter(viewToPresenter);
                fragmentManager.beginTransaction().replace(R.id.main_frame, archiveFrag).commit();
                break;
        }
    }

    private void initList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.nav_bar_list_item
                , NavBarItems);
        navBarList.setAdapter(adapter);
        navBarList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        navBarList.setItemChecked(0, true);
        navBarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navBarList.setItemChecked(position, true);
                getSupportActionBar().setTitle(NavBarItems[position]);
                if (position == currentPagePosition)
                    return;

                setPageByPosition(position);

                currentPagePosition = position;
                retainFrag.setCurrentPagePosition(currentPagePosition);
                drawerLayout.closeDrawer(Gravity.START);
            }
        });
    }

    private void initNavDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.open_nav, R.string.closed_nav) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                fab.setRotation(180 * slideOffset);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);


    }

    // letters received
    @Override
    public void retrieveNewLetterSuccess(ArrayList<Model.Letter> letters, String result) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        if (currentPagePosition == 0)
            unReadFrag.passLetters(letters);
    }

    @Override
    public void retrieveNewLettersFailed() {
        Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void retrieveReadLettersSuccess(ArrayList<Model.Letter> readLetters, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        if (currentPagePosition == 1)
            readFrag.passLetters(readLetters);
    }

    @Override
    public void retrieveArchiveLettersSuccess(ArrayList<Model.Letter> archiveLetters, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        if (currentPagePosition == 2)
            archiveFrag.passLetters(archiveLetters);
    }

    @Override
    public void retrieveReadLettersFailed() {
        Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void retrieveArchiveLettersFailed() {
        Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void hideFab() {
        fab.hide();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        presenter.detach();
        super.onDestroy();
    }

    @OnClick(R.id.new_letter_fab)
    public void FabClicked() {
        startActivity(new Intent(getApplicationContext(), NewLetterActivity.class));
    }

    @Override
    public void FragAttached() {
        if (fab != null)
            fab.hide();
    }

    @Override
    public void FragDetached() {
        if (fab != null)
            fab.show();
    }
}

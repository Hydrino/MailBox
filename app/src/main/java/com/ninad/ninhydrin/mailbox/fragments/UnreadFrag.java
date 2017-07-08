package com.ninad.ninhydrin.mailbox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ninad.ninhydrin.mailbox.MVP;
import com.ninad.ninhydrin.mailbox.Model;
import com.ninad.ninhydrin.mailbox.R;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UnreadFrag extends Fragment {


    @BindView(R.id.unread_frame)
    FrameLayout frameLayout;

    private Context context;
    private ArrayList<LetterCard> cards;
    private float cX, cY;
    private MVP.ViewToPresenter viewToPresenter;
    private String RecipientID;
    // layout parameters for letter cards
    private final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
            (500), 300);

    private int[] colours = {R.color.letter1, R.color.letter2,
            R.color.letter3, R.color.letter4, R.color.letter5, R.color.letter6,
            R.color.letter7, R.color.letter8};

    private SpringAnimation springX;
    private SpringAnimation springY;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        cards = new ArrayList<>();
        layoutParams.gravity = Gravity.CENTER;
        SharedPreferences God = context.getSharedPreferences(getString(R.string.god), Context.MODE_PRIVATE);
        RecipientID = God.getString(getString(R.string.user_id), "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unread, container, false);
        ButterKnife.bind(this, view);

        // get center of the frame layout
        getScreenCenter();

        // send signal to retrieve letters
        // important to call this method after view is created
        // because we show the result in UI
        if (RecipientID != null && !RecipientID.equals("") && viewToPresenter != null) {
            viewToPresenter.retrieveNewLetters(RecipientID);
        }

        return view;
    }


    // letters are passed from model
    public void passLetters(ArrayList<Model.Letter> letters) {
        // give letter to individual letter card
        for (int i = 0; i < letters.size(); i++) {
            cards.add(new LetterCard(context, letters.get(i)));
        }

        // show these passed letters to user
        showLetters();
    }

    private void showLetters() {
        // add letter cards to frame layout
        addCards(getTouchListener());
        viewToPresenter.LettersShownSuccess();
    }

    private void addCards(View.OnTouchListener listener) {
        for (int i = 0; i < cards.size(); i++) {

            cards.get(i).setLayoutParams(layoutParams);
            cards.get(i).setOnTouchListener(listener);

            cards.get(i).setCardElevation(2 * (i + 1));
            int colour = i % 8;
            cards.get(i).setBackgroundColor(ContextCompat.getColor(context, colours[colour]));
            cards.get(i).setTranslationY(((cards.size() - 1) - i) * 7);
            cards.get(i).setRotation(-20 + new Random().nextInt(41));
            float scale = 0.90f + (float) ((0.1 / cards.size()) * (i + 1));
            cards.get(i).setScaleX(scale);

            frameLayout.addView(cards.get(i), i);
        }
    }

    private View.OnTouchListener getTouchListener() {
        return new View.OnTouchListener() {
            float lastX, lastY, viewX, viewY, downTime, upTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                springX = new SpringAnimation(v, DynamicAnimation.TRANSLATION_X);
                springY = new SpringAnimation(v, DynamicAnimation.TRANSLATION_Y);

                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    downTime = SystemClock.currentThreadTimeMillis();
                }


                if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    float dX = event.getRawX() - lastX;
                    float dY = event.getRawY() - lastY;
                    v.setTranslationX(dX + v.getTranslationX());
                    v.setTranslationY(dY + v.getTranslationY());
                }

                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    upTime = SystemClock.currentThreadTimeMillis();

                    // clicked
                    if (Math.abs(downTime - upTime) < 20 && (Math.abs(cX - viewX) < 5 || Math.abs(cY - viewY) < 5)) {
                        showLetter(v);
                        return true;
                    }

                    if (Math.abs(cX - viewX) > 300 || Math.abs(cY - viewY) > 400) {
                        viewToPresenter.LetterSwiped(frameLayout.indexOfChild(v), ((LetterCard) v).getLetter().getId());
                        frameLayout.removeView(v);
                        Toast.makeText(context, "Archived", Toast.LENGTH_SHORT).show();

                    } else {
                        springX.animateToFinalPosition(0);
                        springY.animateToFinalPosition(0);
                    }

                }

                viewY = v.getY() + v.getHeight() / 2;
                viewX = v.getX() + v.getWidth() / 2;
                lastX = event.getRawX();
                lastY = event.getRawY();

                return true;
            }


        };
    }


    private void showLetter(View v) {
        final int index = frameLayout.indexOfChild(v);
        final int id = cards.get(index).getLetter().getId();
        Bundle bundle = new Bundle();

        bundle.putString(getString(R.string.letter_date), cards.get(index).getDate());
        bundle.putString(getString(R.string.letter_name), cards.get(index).getSenderName());
        bundle.putString(getString(R.string.letter_body), cards.get(index).getBody());

        final LetterFrag letterFrag = new LetterFrag();
        letterFrag.setArguments(bundle);

        cards.clear();

        frameLayout.removeAllViews();

        viewToPresenter.LetterOpened(index, id);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, letterFrag);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void getScreenCenter() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cX = frameLayout.getMeasuredWidth() / 2;
                cY = frameLayout.getMeasuredHeight() / 2;
            }
        }, 500);
    }

    public void setViewToPresenter(MVP.ViewToPresenter viewToPresenter) {
        this.viewToPresenter = viewToPresenter;
    }

}

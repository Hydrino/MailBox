package com.ninad.ninhydrin.mailbox.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ninad.ninhydrin.mailbox.Model;

public class LetterCard extends CardView {

    private Model.Letter letter;
    private String SenderName, SenderID, Body, Date;
    private LinearLayout linearLayout;
    private TextView SenderNameTextView, SenderIDTextView, DateTextView;

    public String getSenderName() {
        return SenderName;
    }

    public String getBody() {
        return Body;
    }

    public String getDate() {
        return Date;
    }

    public Model.Letter getLetter() {
        return letter;
    }

    Paint paint;

    public LetterCard(Context context) {
        super(context);
    }

    public LetterCard(Context context, Model.Letter letter) {
        super(context);
        this.letter = letter;
        paint = new Paint();
        setUpTextViews(context);
        setUpAndAddLinearLayout(context);
        addTextViews();
    }

    private void setUpAndAddLinearLayout(Context context) {
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LetterCard.LayoutParams params = new LetterCard.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setPadding(16, 16, 16, 16);
        addView(linearLayout);
    }

    private void setUpTextViews(Context context) {
        SenderIDTextView = new TextView(context);
        SenderNameTextView = new TextView(context);
        DateTextView = new TextView(context);

        SenderName = letter.getSenderName();
        SenderID = letter.getSenderID();
        Body = letter.getBody();
        Date = letter.getDate();

        SenderNameTextView.setText(SenderName);
        SenderIDTextView.setText(SenderID);
        DateTextView.setText(Date);

        SenderIDTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        SenderNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        DateTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

    }

    private void addTextViews() {
        linearLayout.addView(SenderIDTextView);
        linearLayout.addView(SenderNameTextView);
        linearLayout.addView(DateTextView);

    }
}

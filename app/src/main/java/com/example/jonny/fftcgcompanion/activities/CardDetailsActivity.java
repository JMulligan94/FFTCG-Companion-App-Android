package com.example.jonny.fftcgcompanion.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.models.Card;
import com.example.jonny.fftcgcompanion.utils.FFTCGCompanionApplication;
import com.example.jonny.fftcgcompanion.utils.GeneralUtils;
import com.example.jonny.fftcgcompanion.views.ViewCardsFragment;

public class CardDetailsActivity extends Activity
{
    private Card m_card = null;
    private TextView m_cardCounterTextView = null;

    private boolean m_selectCard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_details_activity);

        Intent intent = getIntent();
        String cardID = intent.getStringExtra("CardID");
        m_selectCard = intent.getBooleanExtra("SelectCard", false);

        if (m_selectCard)
        {
            findViewById(R.id.counter_layout).setVisibility(View.GONE);
            findViewById(R.id.selectcard_layout).setVisibility(View.VISIBLE);
        }

        m_card = FFTCGCompanionApplication.getCardRepository().getCard(cardID);
        if (m_card != null)
        {
            findViewById(R.id.titlebar_frame)
                    .setBackground(GeneralUtils
                            .getGradientDrawable(10, getResources().getColor(
                                    R.color.colorBackgroundLight), getResources().getColor(R.color.dark),
                                    15, 15, 0, 0));

            ImageView cardImageView = findViewById(R.id.card_details_imageview);
            Bitmap cardArtwork = m_card.getArtworkLocally(this);
            if (cardArtwork != null)
            {
                cardImageView.setImageBitmap(cardArtwork);
            }
            else
            {
                // Default card artwork
                cardImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_icon));
            }

            TextView nameTextView = findViewById(R.id.name_textview);
            nameTextView.setText(m_card.getName() + " (" + m_card.getId() + ")");

            TextView costTextView = findViewById(R.id.cost_textview);
            costTextView.setText(String.valueOf(m_card.getCost()));

            // Set background and foreground colours to element colours
            costTextView.setBackgroundColor(getResources().getIntArray(R.array.cardelement_colours)[m_card.getElement().ordinal()]);
            costTextView.setTextColor(getResources().getIntArray(R.array.cardelement_foregroundcolours)[m_card.getElement().ordinal()]);

            TextView subTextView = findViewById(R.id.sub_textview);
            subTextView.setText(String.valueOf(m_card.getType() + " - " + m_card.getJob() + " - " + m_card.getCategory()));

            TextView descTextView = findViewById(R.id.desc_textview);
            descTextView.setText(m_card.getDescription());
            descTextView.setMovementMethod(new ScrollingMovementMethod());

            (findViewById(R.id.desc_scrollview)).setBackground(
                    GeneralUtils.getGradientDrawable(0, 10,
                            getResources().getColor(R.color.colorBackgroundDark),
                            getResources().getColor(R.color.dark)));

            findViewById(R.id.counter_layout).setBackground(GeneralUtils
                    .getGradientDrawable(10,
                            getResources().getColor(R.color.colorBackgroundLight),
                            getResources().getColor(R.color.dark),
                            0, 0, 15, 15));

            m_cardCounterTextView = findViewById(R.id.card_count_textview);
            m_cardCounterTextView.setText(String.valueOf(m_card.getCount()));
        }
    }

    public void onDecrement(View view)
    {
        m_card.decrementCount();
        m_cardCounterTextView.setText(String.valueOf(m_card.getCount()));
    }

    public void onIncrement(View view)
    {
        m_card.incrementCount();
        m_cardCounterTextView.setText(String.valueOf(m_card.getCount()));
    }

    public void onClose(View view)
    {
        finish();
    }

    public void onSelectCard(View view)
    {
        int quantity = 0;
        switch (view.getId())
        {
            case R.id.select1button:
            {
                quantity = 1;
            }
            break;
            case R.id.select2button:
            {
                quantity = 2;
            }
            break;
            case R.id.select3button:
            {
                quantity = 3;
            }
            break;
        }
        Intent intent = new Intent();
        intent.putExtra("CardID", m_card.getId());
        intent.putExtra("Quantity", quantity);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}

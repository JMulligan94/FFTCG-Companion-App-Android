package com.example.jonny.fftcgcompanion.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.jonny.fftcgcompanion.utils.FilterLinearLayout;
import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.models.Card;
import com.example.jonny.fftcgcompanion.models.CardFilterParcel;
import com.example.jonny.fftcgcompanion.utils.OnFilterItemSelectedListener;

public class CardFilterActivity extends Activity
{
    private CardFilterParcel m_filterParcel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_filter_activity);

        m_filterParcel = (CardFilterParcel) getIntent().getSerializableExtra("FilterParcel");

        // Card Owned
        FilterLinearLayout cardOwnedLayout = findViewById(R.id.cardowned_listlayout);
        cardOwnedLayout.populateLayout(new OnFilterItemSelectedListener()
        {
            @Override
            public boolean isFilterItemSelected(int position)
            {
                return m_filterParcel.ParcelCardOwned.contains(Card.CardOwned.values()[position]);
            }

            @Override
            public void onFilterItemSelected(int position, boolean selected)
            {
                Card.CardOwned cardOwned = Card.CardOwned.values()[position];
                if (selected)
                {
                    m_filterParcel.ParcelCardOwned.add(cardOwned);
                }
                else
                {
                    m_filterParcel.ParcelCardOwned.remove(cardOwned);
                }
            }
        });

        // Card Type
        final String[] cardTypes = getResources().getStringArray(R.array.filter_type_array);
        FilterLinearLayout cardTypeLayout = findViewById(R.id.cardtype_listlayout);
        cardTypeLayout.populateLayout(new OnFilterItemSelectedListener()
        {
            @Override
            public boolean isFilterItemSelected(int position)
            {
                Card.CardType cardType = Card.CardType.valueOf(cardTypes[position].toUpperCase());
                return m_filterParcel.ParcelCardTypes.contains(cardType);
            }

            @Override
            public void onFilterItemSelected(int position, boolean selected)
            {
                Card.CardType cardType = Card.CardType.valueOf(cardTypes[position].toUpperCase());
                if (selected)
                {
                    m_filterParcel.ParcelCardTypes.add(cardType);
                }
                else
                {
                    m_filterParcel.ParcelCardTypes.remove(cardType);
                }
            }
        });


        // Card Element
        final String[] cardElements = getResources().getStringArray(R.array.filter_element_array);
        FilterLinearLayout cardElementLayout = findViewById(R.id.cardelement_listlayout);
        cardElementLayout.populateLayout(new OnFilterItemSelectedListener()
        {
            @Override
            public boolean isFilterItemSelected(int position)
            {
                Card.CardElement cardElement = Card.CardElement.valueOf(cardElements[position].toUpperCase());
                return m_filterParcel.ParcelCardElements.contains(cardElement);
            }

            @Override
            public void onFilterItemSelected(int position, boolean selected)
            {
                Card.CardElement cardElement = Card.CardElement.valueOf(cardElements[position].toUpperCase());
                if (selected)
                {
                    m_filterParcel.ParcelCardElements.add(cardElement);
                }
                else
                {
                    m_filterParcel.ParcelCardElements.remove(cardElement);
                }
            }
        });


        // Card Opus
        FilterLinearLayout cardOpusLayout = findViewById(R.id.cardopus_listlayout);
        cardOpusLayout.populateLayout(new OnFilterItemSelectedListener()
        {
            @Override
            public boolean isFilterItemSelected(int position)
            {
                Card.CardOpus cardOpus = Card.CardOpus.values()[position];
                return m_filterParcel.ParcelOpus.contains(cardOpus);
            }

            @Override
            public void onFilterItemSelected(int position, boolean selected)
            {
                Card.CardOpus cardOpus = Card.CardOpus.values()[position];
                if (selected)
                {
                    m_filterParcel.ParcelOpus.add(cardOpus);
                }
                else
                {
                    m_filterParcel.ParcelOpus.remove(cardOpus);
                }
            }
        });

        // Card Cost
        FilterLinearLayout cardCostLayout = findViewById(R.id.cardcost_listlayout);
        cardCostLayout.populateLayout(new OnFilterItemSelectedListener()
        {
            @Override
            public boolean isFilterItemSelected(int position)
            {
                return m_filterParcel.ParcelCost.contains(position);
            }

            @Override
            public void onFilterItemSelected(int position, boolean selected)
            {
                if (selected)
                {
                    m_filterParcel.ParcelCost.add(position);
                }
                else
                {
                    m_filterParcel.ParcelCost.remove(position);
                }
            }
        });
    }

    public void onSearch(View view)
    {
        Intent intent = new Intent();
        intent.putExtra("Filters", m_filterParcel);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void onClear(View view)
    {
        m_filterParcel.resetFilters();

        Intent intent = new Intent();
        intent.putExtra("Filters", m_filterParcel);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}

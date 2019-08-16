package com.example.jonny.fftcgcompanion.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.utils.OnCardSelectedListener;
import com.example.jonny.fftcgcompanion.views.ViewCardsFragment;

public class AddCardActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        ViewCardsFragment viewCardsFragment = ViewCardsFragment.newInstance();
        viewCardsFragment.setResultType(ViewCardsFragment.ViewCardsResult.SELECT_CARD);
        viewCardsFragment.setCardSelectedListener(new OnCardSelectedListener()
        {
            @Override
            public void onCardSelected(String cardID, int quantity)
            {
                Intent intent = new Intent();
                intent.putExtra("SelectedCardID", cardID);
                intent.putExtra("Quantity", quantity);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        transaction.replace(R.id.card_viewer_fragment_framelayout, viewCardsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

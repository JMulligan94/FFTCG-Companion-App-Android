package com.example.jonny.fftcgcompanion.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.jonny.fftcgcompanion.models.Card;
import com.example.jonny.fftcgcompanion.models.CardRepository;
import com.example.jonny.fftcgcompanion.views.ViewCardsFragment;

import java.util.List;

public class ViewCardsViewModel extends ViewModel
{
    private MutableLiveData<List<Card>> m_cards;

    public ViewCardsViewModel()
    {
    }

    public LiveData<List<Card>> getCards()
    {
        if (m_cards == null)
        {
            m_cards = new MutableLiveData<>();
            m_cards.setValue(CardRepository.getCardList(true));
        }
        return m_cards;
    }
}

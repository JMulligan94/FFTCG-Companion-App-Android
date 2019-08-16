package com.example.jonny.fftcgcompanion.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.jonny.fftcgcompanion.utils.FFTCGCompanionApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CardFilterParcel implements Serializable
{
    public List<String> ParcelKeywords;
    public List<Card.CardType> ParcelCardTypes;
    public List<Card.CardElement> ParcelCardElements;
    public List<Card.CardOpus> ParcelOpus;
    public List<Card.CardOwned> ParcelCardOwned;
    public List<Integer> ParcelCost;
    public List<String> ParcelCategories;

    public CardFilterParcel()
    {
        resetFilters();
    }

    public boolean hasSearchParameters()
    {
        return !ParcelKeywords.isEmpty()
                || !ParcelCardTypes.isEmpty()
                || !ParcelCardElements.isEmpty()
                || !ParcelOpus.isEmpty()
                || !ParcelCardOwned.isEmpty()
                || !ParcelCategories.isEmpty()
                || !ParcelCost.isEmpty();
    }

    public Vector<Card> searchCards()
    {
        List<Card> cards = CardRepository.getCardList(true);
        Vector<Card> filteredCards = new Vector<>();

        for(Card card : cards)
        {
            if (matchesKeywords(card)
                    && matchesTypes(card)
                    && matchesElements(card)
                    && matchesOpus(card)
                    && matchesOwned(card)
                    && matchesCost(card))
            {
                filteredCards.add(card);
            }
        }

        return filteredCards;
    }

    public void resetFilters()
    {
        ParcelKeywords = new ArrayList<>();
        ParcelCardTypes = new ArrayList<>();
        ParcelCardElements = new ArrayList<>();
        ParcelOpus = new ArrayList<>();
        ParcelCategories = new ArrayList<>();
        ParcelCost = new ArrayList<>();
        ParcelCardOwned = new ArrayList<>();

        // Default to both owned and unowned
        ParcelCardOwned.add(Card.CardOwned.CARD_OWNED);
        ParcelCardOwned.add(Card.CardOwned.CARD_UNOWNED);
    }

    private boolean matchesKeywords(Card card)
    {
        for(String keyword : ParcelKeywords)
        {
            if (!card.getId().toLowerCase().contains(keyword.toLowerCase())
                    && !card.getName().toLowerCase().contains(keyword.toLowerCase()))  {
                return false;
            }
        }
        return true;
    }

    private boolean matchesTypes(Card card)
    {
        if(ParcelCardTypes.isEmpty())
            return true;

        for(Card.CardType type : ParcelCardTypes)
        {
            if (card.getType() == type)
            {
                return true;
            }
        }
        return false;
    }

    private boolean matchesElements(Card card)
    {
        if(ParcelCardElements.isEmpty())
            return true;

        for(Card.CardElement element : ParcelCardElements)
        {
            if (card.getElement() == element)
            {
                return true;
            }
        }
        return false;
    }

    private boolean matchesOpus(Card card)
    {
        if(ParcelOpus.isEmpty())
            return true;

        for(Card.CardOpus opus : ParcelOpus)
        {
            if (card.getOpus() == opus)
            {
                return true;
            }
        }
        return false;
    }

    private boolean matchesCost(Card card)
    {
        if(ParcelCost.isEmpty())
            return true;

        for(Integer cost : ParcelCost)
        {
            if (card.getCost() == cost)
            {
                return true;
            }
        }
        return false;
    }

    private boolean matchesOwned(Card card)
    {
        if (ParcelCardOwned.isEmpty()
                || ParcelCardOwned.size() == 2)
            return true;

        if (ParcelCardOwned.contains(Card.CardOwned.CARD_OWNED))
        {
            return card.getCount() > 0;
        }
        else
        {
            return card.getCount() == 0;
        }
    }
}

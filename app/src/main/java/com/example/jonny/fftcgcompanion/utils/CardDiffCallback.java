package com.example.jonny.fftcgcompanion.utils;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.example.jonny.fftcgcompanion.models.Card;

import java.util.List;

////////////////////////////////////////////////
// class CardDiffCallback
////////////////////////////////////////////////
public class CardDiffCallback extends DiffUtil.Callback
{
    private final List<Card> m_oldCardList;
    private final List<Card> m_newCardList;

    public CardDiffCallback(List<Card> oldCardList, List<Card> newCardList)
    {
        m_oldCardList = oldCardList;
        m_newCardList = newCardList;
    }

    @Override
    public int getOldListSize()
    {
        return m_oldCardList.size();
    }

    @Override
    public int getNewListSize()
    {
        return m_newCardList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
    {
        return m_oldCardList.get(oldItemPosition).getId() == m_newCardList.get(
                newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {
        return m_oldCardList.get(oldItemPosition).getName().equals(m_newCardList.get(
                newItemPosition).getName());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

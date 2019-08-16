package com.example.jonny.fftcgcompanion.utils;

import android.app.Application;
import android.content.Context;

import com.example.jonny.fftcgcompanion.models.CardRepository;

public class FFTCGCompanionApplication extends Application
{
    private static Context m_context;

    private static CardRepository m_cardRepository;

    public void onCreate() {
        super.onCreate();
        FFTCGCompanionApplication.m_context = getApplicationContext();

        // Set up the card repository
        m_cardRepository = new CardRepository();
    }

    public static Context getAppContext() {
        return FFTCGCompanionApplication.m_context;
    }

    public static CardRepository getCardRepository()
    {
        return FFTCGCompanionApplication.m_cardRepository;
    }
}

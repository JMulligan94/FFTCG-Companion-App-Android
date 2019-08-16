package com.example.jonny.fftcgcompanion.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import com.example.jonny.fftcgcompanion.models.Card;
import com.example.jonny.fftcgcompanion.models.CardRepository;
import com.example.jonny.fftcgcompanion.utils.FFTCGCompanionApplication;
import com.example.jonny.fftcgcompanion.utils.GeneralUtils;

import java.util.List;


public class SettingsViewModel extends ViewModel
{
    public ObservableInt DownloadedCount = new ObservableInt(0);
    public ObservableInt DownloadedMax = new ObservableInt(0);
    public ObservableBoolean IsDownloading = new ObservableBoolean(false);
    public ObservableField<String> ProgressText = new ObservableField<>();

    public SettingsViewModel()
    {
    }

    public void downloadCards(View view)
    {
        if (!GeneralUtils.isConnectedToWifi())
            return;

        DownloadedCount.set(0);
        IsDownloading.set(true);

        final List<Card> cards = CardRepository.getCardList(true);
        DownloadedMax.set(cards.size());

        updateProgressText(null);

        for (int i = 0; i < cards.size(); ++i)
        {
            final Card card = cards.get(i);
            if (card.getArtworkLocally(FFTCGCompanionApplication.getAppContext()) != null)
            {
                DownloadedCount.set(DownloadedCount.get() + 1);
                updateProgressText(card);

                if (DownloadedCount.get() == cards.size())
                {
                    IsDownloading.set(false);
                    Toast.makeText(FFTCGCompanionApplication.getAppContext(), "Download complete", Toast.LENGTH_LONG);
                }
            }
            else
            {
                card.retrieveArtwork(FFTCGCompanionApplication.getAppContext(), new Card.OnCardArtworkRetrievalListener()
                {
                    @Override
                    public void OnCardArtworkRetrieved(Bitmap artwork)
                    {
                        DownloadedCount.set(DownloadedCount.get() + 1);
                        updateProgressText(card);

                        if (DownloadedCount.get() == cards.size())
                        {
                            IsDownloading.set(false);
                            Toast.makeText(FFTCGCompanionApplication.getAppContext(), "Download complete", Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        }
    }

    private void updateProgressText(Card card)
    {
        String progressText = "Downloaded (" + DownloadedCount.get() + "/"
                + DownloadedMax.get() + ") cards. "
                + (DownloadedMax.get() - DownloadedCount.get()) + " remaining.";

        if (card != null)
        {
            progressText += "\n" + card.getId() + " (" + card.getName() + ")";
        }

        ProgressText.set(progressText);
    }

    public void clearCards(View view)
    {
        FFTCGCompanionApplication.getCardRepository().clearCachedCards(view);
    }

    public void clearCollection(View view)
    {
        FFTCGCompanionApplication.getCardRepository().clearCardCollection();
        Toast.makeText(FFTCGCompanionApplication.getAppContext(), "Cleared card collection", Toast.LENGTH_SHORT);
    }
}

package com.example.jonny.fftcgcompanion.viewmodels;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.example.jonny.fftcgcompanion.activities.CreateDeckActivity;
import com.example.jonny.fftcgcompanion.activities.ViewDecksActivity;
import com.example.jonny.fftcgcompanion.utils.FFTCGCompanionApplication;
import com.example.jonny.fftcgcompanion.utils.GeneralUtils;

public class DecksViewModel extends ViewModel
{
    public ObservableBoolean openCollectionFilePicker = new ObservableBoolean(false);

    public void createDeck(View view)
    {
        Intent intent = new Intent(view.getContext(), CreateDeckActivity.class);
        view.getContext().startActivity(intent);
    }

    public void viewDecks(View view)
    {
        Intent intent = new Intent(view.getContext(), ViewDecksActivity.class);
        view.getContext().startActivity(intent);
    }

    public void exportCardCollection(View view)
    {
        if (!GeneralUtils.isExternalStorageWritable())
            return;

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext());
        alertBuilder.setTitle("Name");

        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertBuilder.setView(input);

        alertBuilder.setPositiveButton("Export", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                FFTCGCompanionApplication.getCardRepository().exportAsCSV(input.getText().toString());
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        alertBuilder.show();
    }

    public void importCardCollection(View view)
    {
        openCollectionFilePicker.set(true);
    }
}

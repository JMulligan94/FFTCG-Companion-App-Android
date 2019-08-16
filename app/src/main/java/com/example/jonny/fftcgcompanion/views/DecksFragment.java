package com.example.jonny.fftcgcompanion.views;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.databinding.DecksFragmentBinding;
import com.example.jonny.fftcgcompanion.utils.FFTCGCompanionApplication;
import com.example.jonny.fftcgcompanion.viewmodels.DecksViewModel;

import static android.app.Activity.RESULT_OK;

public class DecksFragment extends Fragment
{
    private DecksViewModel m_viewModel;
    private DecksFragmentBinding m_binding;

    private static final int IMPORT_RESULT_CODE = 1;

    public static DecksFragment newInstance()
    {
        return new DecksFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        m_binding = DataBindingUtil.inflate(inflater, R.layout.decks_fragment, container, false);
        m_viewModel = ViewModelProviders.of(this).get(DecksViewModel.class);
        m_viewModel.openCollectionFilePicker.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback()
        {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId)
            {
                if (m_viewModel.openCollectionFilePicker.get())
                {
                    promptChooseFile();
                    m_viewModel.openCollectionFilePicker.set(false);
                }
            }
        });
        m_binding.setDecks(m_viewModel);
        return m_binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    public void promptChooseFile()
    {
        // If SDK >= 19, use standard file manager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            startActivityForResult(intent, IMPORT_RESULT_CODE);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            startActivityForResult(intent, IMPORT_RESULT_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == IMPORT_RESULT_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                FFTCGCompanionApplication.getCardRepository().importCollectionFromCSV(getContext(), data.getData());
            }
        }
    }
}

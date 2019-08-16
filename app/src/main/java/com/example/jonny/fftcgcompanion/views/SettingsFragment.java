package com.example.jonny.fftcgcompanion.views;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.viewmodels.SettingsViewModel;

import com.example.jonny.fftcgcompanion.databinding.SettingsFragmentBinding;

public class SettingsFragment extends Fragment
{
    private SettingsViewModel m_viewModel;
    private SettingsFragmentBinding m_binding;
    public static SettingsFragment newInstance()
    {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        m_binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false);
        m_viewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        m_binding.setSettings(m_viewModel);
        return m_binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

    }

}

package com.application.mgoaplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.application.mgoaplication.R;

public class DefaultFragment extends Fragment {

    public DefaultFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView           = inflater.inflate(R.layout.fragment_home, container, false);

        return rootView;
    }
}
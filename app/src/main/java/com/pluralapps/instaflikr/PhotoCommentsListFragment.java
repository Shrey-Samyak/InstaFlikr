package com.pluralapps.instaflikr;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.pluralapps.instaflikr.interfaces.InitialSetup;
import com.pluralapps.instaflikr.parcelabletypes.Photo;

public class PhotoCommentsListFragment extends SherlockFragment implements InitialSetup {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;

        v = inflater.inflate(R.layout.photos_comments_list, container, false);
        if(v != null)
            return v;

        return null;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    @Override
    public void performInitialSetup(View view) {
        Intent i = getSherlockActivity().getIntent();
        Photo photo = (Photo) i.getParcelableExtra("photo");
    }
}

package com.example.fbuapp.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fbuapp.fragments.groupFragments.GroupResourcesFragment;
import com.example.fbuapp.fragments.groupFragments.GroupDetailsFragment;
import com.example.fbuapp.models.Group;

import org.jetbrains.annotations.NotNull;

public class FragmentAdapter extends FragmentStateAdapter {

    Group group;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Group group) {
        super(fragmentManager, lifecycle);
        this.group = group;
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 1 :
                Bundle bundle = new Bundle();
                GroupResourcesFragment resourcesFragment = new GroupResourcesFragment();
                bundle.putParcelable("itemGroup", group);
                resourcesFragment.setArguments(bundle);
                return resourcesFragment;
        }
        Bundle bundle = new Bundle();
        GroupDetailsFragment detailsFragment = new GroupDetailsFragment();
        bundle.putParcelable("itemGroup", group);
        detailsFragment.setArguments(bundle);
        return detailsFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

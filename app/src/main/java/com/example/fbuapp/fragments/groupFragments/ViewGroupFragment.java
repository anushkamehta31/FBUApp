package com.example.fbuapp.fragments.groupFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbuapp.R;
import com.example.fbuapp.adapters.TabsAdapter;
import com.example.fbuapp.models.Group;
import com.google.android.material.tabs.TabLayout;

public class ViewGroupFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 pager2;
    TabsAdapter adapter;
    private Group group;

    public ViewGroupFragment() {
        // Required empty public constructor
    }

    public static ViewGroupFragment newInstance(Group group) {
        // Create a fragment with arguments
        ViewGroupFragment fragment = new ViewGroupFragment();
        Bundle args = new Bundle();
        args.putParcelable("itemGroup", group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve arguments passed
        group = (Group) getArguments().getParcelable("itemGroup");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabLayout);
        pager2 = view.findViewById(R.id.viewPager);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        adapter = new TabsAdapter(fm, getLifecycle(), group);
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Resources"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }
}
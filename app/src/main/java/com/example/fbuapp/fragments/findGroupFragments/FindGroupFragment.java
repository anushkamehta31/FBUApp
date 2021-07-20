package com.example.fbuapp.fragments.findGroupFragments;

import android.animation.ArgbEvaluator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fbuapp.R;
import com.example.fbuapp.adapters.SwipeAdapter;
import com.example.fbuapp.databinding.FragmentFindGroupBinding;
import com.example.fbuapp.models.Group;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class FindGroupFragment extends Fragment {

    public boolean virtual;
    FragmentFindGroupBinding binding;
    private ArrayList<String> al;
    public static final String TAG = "FindGroupFragment";
    private ArrayAdapter arrayAdapter;
    private int i;
    private LinearLayout layoutFindIndicators;
    private double maxDistance;
    private ArrayList<String> topics;
    private ArrayList<String> preferences;
    private ArrayList<Group> potentialGroups;
    ViewPager viewPager;
    SwipeAdapter adapter;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();


    public FindGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFindGroupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Virtual or In-Person Group
        // If in person what is the radius?
        // Group type preferences
        // Preset topics and additional topics preferences
        potentialGroups = new ArrayList<>();
        adapter = new SwipeAdapter(potentialGroups, getContext());
        findPotentialMatches();

        viewPager = view.findViewById(R.id.viewPagerFind);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
        };

        colors = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position < (adapter.getCount() -1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                }
                else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // showPotentialMatches();
    }

    private void findPotentialMatches() {
        ParseQuery<Group> queryGroup = new ParseQuery<Group>(Group.class);
        // Where the type of group is equal to the type the user selected
        // queryGroup.whereEqualTo(Group.KEY_VIRTUAL, virtual);
        // Where the group contains at least on of the user's preferred study topics
        // queryGroup.whereContainedIn(Group.KEY_TOPICS, topics);
        // Where the groups contains at least one of the study preference
        queryGroup.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Error finding groups");
                    return;
                }
                potentialGroups.addAll(groups);
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void showPotentialMatches() {
        al = new ArrayList<>();
        al.add("php");
        al.add("c");
        al.add("python");
        al.add("java");
        al.add("html");
        al.add("c++");
        al.add("css");
        al.add("javascript");

        arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.swipe_item_group, R.id.helloText, al);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) binding.swipeFrame;

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    Log.d("LIST", "removed object!");
                    al.remove(0);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {
                    // Do something on the left!
                    // You also have access to the original object.
                    // If you want to use it just cast it (String) dataObject
                    Toast.makeText(getContext(), "Left!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    Toast.makeText(getContext(), "Right!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    // Ask for more data here
                    al.add("XML ".concat(String.valueOf(i)));
                    arrayAdapter.notifyDataSetChanged();
                    Log.d("LIST", "notified");
                    i++;
                }

                @Override
                public void onScroll(float scrollProgressPercent) {

                }
            });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(getContext(), "Click!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
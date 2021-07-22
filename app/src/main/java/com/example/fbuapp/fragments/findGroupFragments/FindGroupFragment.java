package com.example.fbuapp.fragments.findGroupFragments;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbuapp.R;
import com.example.fbuapp.adapters.SwipeAdapter;
import com.example.fbuapp.databinding.FragmentFindGroupBinding;
import com.example.fbuapp.managers.GroupManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.School;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.slider.Slider;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.fbuapp.managers.GroupManager.getDistanceToGroup;


public class FindGroupFragment extends Fragment {

    public boolean virtual;
    public boolean allTypes;
    public boolean sortByDistance;
    public boolean sortByCreatedAt;
    public boolean large;
    public boolean small;
    public boolean average;
    public Map<String, School> potentialSchools;
    FragmentFindGroupBinding binding;
    public static final String TAG = "FindGroupFragment";
    public float maxDistance;
    public School school;
    public ArrayList<String> potentialTopics;
    public List<Group> potentialGroups;
    ViewPager viewPager;
    public SwipeAdapter adapter;
    MaterialButton btnFilter;



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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        virtual = allTypes = sortByCreatedAt = sortByDistance = small = average = large = false;
        maxDistance = 50;
        potentialSchools = new HashMap<String, School>();
        school = new School();
        potentialTopics = new ArrayList<>();

        btnFilter = binding.btnFilter;
        btnFilter.bringToFront();
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog(view);
            }
        });

        potentialGroups = new ArrayList<>();
        adapter = new SwipeAdapter(potentialGroups, getContext());
        findPotentialMatches();

        viewPager = view.findViewById(R.id.viewPagerFind);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);
    }

    private void findPotentialMatches() {
        ParseQuery<Group> queryGroup = new ParseQuery<Group>(Group.class);
        queryGroup.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Error finding groups");
                    return;
                }
                potentialGroups.addAll(groups);
                // Remove the groups that the user is part of
                // Specify which class to query
                ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMappings");
                query.include(Group.KEY_GROUP_ID);
                // Get current users groups only
                query.whereEqualTo(Group.KEY_USER_ID, ParseUser.getCurrentUser());
                // Make sure user is a member of a group (not a pending invitation)
                query.whereEqualTo(Group.KEY_IS_MEMBER, true);
                // Get all the groups and add to the array
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> userMappings, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting groups", e);
                            return;
                        }
                        for (ParseObject mapping : userMappings) {
                            Group group = (Group) mapping.getParseObject(Group.KEY_GROUP_ID);
                            for (int i=0; i < potentialGroups.size(); i++) {
                                if (potentialGroups.get(i).getObjectId().equals(group.getObjectId())) {
                                    potentialGroups.remove(i);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void showFilterDialog(View view) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filter_dialog_layout);

        // TODO: Grab references to every view in the dialog here and set on click listeners to perform actions
        MaterialButton btnVirtual = dialog.findViewById(R.id.btnVirtual);
        MaterialButton btnInPerson = dialog.findViewById(R.id.btnInperson);

        btnInPerson.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (btnVirtual.getStrokeColor() == ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.filter))) {
                    btnVirtual.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.normal)));
                }
                btnInPerson.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.filter)));
            }
        });

        btnVirtual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnInPerson.getStrokeColor() == ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.filter))) {
                    btnInPerson.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.normal)));
                }
                btnVirtual.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.filter)));
            }
        });

        // Create a dropdown for group sizes
        String[] groupSizes = getResources().getStringArray(R.array.group_size);
        ArrayAdapter<String> schoolAdapter =
                new ArrayAdapter<String>(getContext(), R.layout.drop_down_item, groupSizes);
        AutoCompleteTextView autoCompleteTextView = dialog.findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(schoolAdapter);

        // Create a dropdown for schools
        ArrayList<String> schools = new ArrayList<>();
        ParseQuery<School> query = ParseQuery.getQuery(School.class);
        List<ParseUser> groupUsers = new ArrayList<>();
        query.include("name");
        query.findInBackground(new FindCallback<School>() {
            @Override
            public void done(List<School> allSchools, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for (School school : allSchools) {
                    School university = (School) school;
                    String schoolName = university.getName();
                    schools.add(schoolName);
                    potentialSchools.put(schoolName, university);
                }
                // Create dropdown for schools users textview
                ArrayAdapter<String> adapterSchool =
                        new ArrayAdapter<String>(getContext(), R.layout.drop_down_item, schools);
                AutoCompleteTextView schoolAutoComplete = dialog.findViewById(R.id.autoCompleteSchool);
                schoolAutoComplete.setAdapter(adapterSchool);
            }
        });


        // Initialize topics tag
        NachoTextView acTopics = dialog.findViewById(R.id.nTopics);
        NachoTextView nAdditionalTopics = dialog.findViewById(R.id.nAdditionalTopics);
        String[] topics = getResources().getStringArray(R.array.topics_array);
        ArrayAdapter<String> adapterTopics =
                new ArrayAdapter<String>(getContext(), R.layout.drop_down_item, topics);
        acTopics.setAdapter(adapterTopics);

        // Set a newline chip terminator for additional/untagged topics
        ((NachoTextView) dialog.findViewById(R.id.nAdditionalTopics)).addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Log.i(TAG, "Cancel clicked");
            }
        });


        // Show the dialog
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // Slide for distance get
        ((Slider) dialog.findViewById(R.id.slider)).addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
                maxDistance = value;
            }
        });
        
        // Set onclick listener to apply selected filters
        MaterialButton apply = dialog.findViewById(R.id.Apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((btnInPerson.getStrokeColor() == ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.normal))) &&
                        (btnVirtual.getStrokeColor() == ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.normal)))) {
                    allTypes = true;
                } else if (btnVirtual.getStrokeColor() == ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.filter))) {
                    virtual = true;
                }

                // Figure out if we need to sort by a certain feature
                if (((Chip) dialog.findViewById(R.id.chipDistance)).isChecked()) sortByDistance = true;
                if (((Chip) dialog.findViewById(R.id.chipNewest)).isChecked()) sortByCreatedAt = true;

                // Get the school
                if (!((AutoCompleteTextView) dialog.findViewById(R.id.autoCompleteSchool)).equals("Auto")) {
                    school = potentialSchools.get(((AutoCompleteTextView) dialog.findViewById(R.id.autoCompleteSchool)).getText().toString());
                }

                // Get group size
                String tvSize = ((AutoCompleteTextView) dialog.findViewById(R.id.autoCompleteTextView)).getText().toString();
                if (tvSize.equals(getString(R.string.small))) small = true;
                else if (tvSize.equals(getString(R.string.large))) large = true;
                else if (tvSize.equals(getString(R.string.avg))) average = true;

                for (com.hootsuite.nachos.chip.Chip chip : acTopics.getAllChips()) {
                    potentialTopics.add(chip.getText().toString());
                }
                for (com.hootsuite.nachos.chip.Chip chip : nAdditionalTopics.getAllChips()) {
                    potentialTopics.add(chip.getText().toString());
                }
                dialog.dismiss();
                goApplyFilters();
            }
        });
    }

    private void goApplyFilters() {
        List<Group> found = new ArrayList<>();
        for (int i = 0; i < potentialGroups.size(); i++) {
            Group currentGroup = potentialGroups.get(i);

            if (allTypes == false) {
                if (virtual && !currentGroup.isVirtual() || (!virtual && currentGroup.isVirtual())) {
                    found.add(currentGroup);
                    continue;
                }
            }

            // Check if group is within distance
            if (!currentGroup.isVirtual()) {
                try {
                    double distance = GroupManager.getDistanceToGroup(currentGroup);
                    float f = (float) distance;
                    if (f > maxDistance) {
                        found.add(currentGroup);
                        continue;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // Check if the group is the correct size
            int members = GroupManager.getMemberCount(currentGroup);
            if ((small && members > 10) || (large && members < 20) || (average && members > 20) || (average && members < 10)) {
                found.add(currentGroup);
                continue;
            }


            ParseQuery<School> currentSchoolQuery = new ParseQuery<School>(School.class);
            currentSchoolQuery.include(School.KEY_NAME);
            currentSchoolQuery.whereEqualTo("objectId", currentGroup.getSchool().getObjectId());
            try {
                School curSchools = currentSchoolQuery.getFirst();
                // Check if the school is correct
                if (school != null && (!curSchools.getName().equals(school.getName()))) {
                    found.add(currentGroup);
                    continue;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Check if at least one topics is present in list
            if ((potentialTopics.size() != 0) && Collections.disjoint(potentialTopics, currentGroup.getTopics())) {
                // No topics in common
                found.add(currentGroup);
                continue;
            }
        }

        // Remove found collections
        potentialGroups.removeAll(found);

        // Sort by correct field
        if (sortByCreatedAt) {
            Collections.sort(potentialGroups, new CustomComparator());
            Collections.reverse(potentialGroups);
        }

        if (sortByDistance) {
            Collections.sort(potentialGroups, new Comparator<Group>() {
                @Override
                public int compare(Group u1, Group u2) {
                    double d1 = 0;
                    double d2 = 0;
                    if (u1.isVirtual() || u2.isVirtual()) return 0;
                    try {
                        d1 = GroupManager.getDistanceToGroup(u1);
                        d2 = GroupManager.getDistanceToGroup(u2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return (int) (d1 - d2);
                }
            });
            Collections.reverse(potentialGroups);
        }

        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
    }

    public class CustomComparator implements Comparator<Group> {
        @Override
        public int compare(Group g1, Group g2) {
            return g1.getCreatedAt().compareTo(g2.getCreatedAt());
        }
    }
}
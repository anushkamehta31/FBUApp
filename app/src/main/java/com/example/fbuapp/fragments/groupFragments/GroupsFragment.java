package com.example.fbuapp.fragments.groupFragments;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.adapters.GroupsAdapter;
import com.example.fbuapp.managers.GroupManager;
import com.example.fbuapp.managers.GroupMappingsManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.GroupMappings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class GroupsFragment extends Fragment {

    protected RecyclerView rvGroups;
    public static final String TAG = "GroupsFragment";
    public List<Group> userGroups;
    public GroupsAdapter adapter;
    FloatingActionButton btnCreate;
    GroupMappingsManager mappingsManager;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvGroups = view.findViewById(R.id.rvGroups);

        userGroups = new ArrayList<>();
        adapter = new GroupsAdapter(getContext(), userGroups);

        rvGroups.setAdapter(adapter);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        // final GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        rvGroups.setLayoutManager(layout);
        // Get the groups
        GroupManager groupManager = new GroupManager();
        groupManager.queryGroups(adapter, userGroups);

        mappingsManager = new GroupMappingsManager();

        // Create group button initialization
        btnCreate = view.findViewById(R.id.btnCreate);
        btnCreate.show();
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCreateGroup();
            }
        });

        // Create item touch helper to register delete swipes to the database
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            // This method is intended for dragging items/rearranging rows within the recycler view
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // This is for registering swipes on items in the recycler view
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                // Get a reference to the group
                int position = viewHolder.getAdapterPosition();
                Group group = userGroups.get(position);

                // Delete the mapping
                mappingsManager.deleteMapping(group, ParseUser.getCurrentUser());

                // Remove the group from the view
                userGroups.remove(position);
                adapter.notifyItemRemoved(position);

                // Display a snackbar to show the action
                Snackbar.make(view, "You left "+group.getName(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userGroups.add(position, group);
                                adapter.notifyItemInserted(position);
                                // Set a new user mapping
                                mappingsManager.setUserMapping(ParseUser.getCurrentUser(), group);
                            }
                        })
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.zm_notification_font_red))
                        .addSwipeLeftActionIcon(R.drawable.outline_delete_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // Attach item touch helper to the recycler view
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvGroups);

    }

    private void goCreateGroup() {
        MainActivity activity = (MainActivity) getContext();
        FragmentManager ft = activity.getSupportFragmentManager();
        CreateGroupFragment fragment = new CreateGroupFragment();
        // Launch create group dialog fragment and set the target fragment for later use when
        // sending results
        fragment.setTargetFragment(GroupsFragment.this, 300);
        fragment.show(ft, "fragment_create_group");
    }


}
package com.example.fbuapp.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.adapters.GridAdapter;
import com.example.fbuapp.adapters.LinksAdapter;
import com.example.fbuapp.adapters.VideoAdapter;
import com.example.fbuapp.fragments.groupFragments.GroupsFragment;
import com.example.fbuapp.fragments.resources.LinksFragment;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Image;
import com.example.fbuapp.models.Link;
import com.example.fbuapp.models.Video;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class ResourceManager {

    public static final String TAG = "ResourceManager";


    public void queryImages(Group group, List<Image> groupImages, GridAdapter gridAdapter) {
        // Specify which class to query
        ParseQuery<Image> query = ParseQuery.getQuery(Image.class);
        // Specify what other data we would like to get back
        query.include(Image.KEY_GROUP);
        // Get current users posts only
        query.whereEqualTo(Image.KEY_GROUP, group);
        // Get most recent posts at top
        query.addDescendingOrder(Image.KEY_CREATED_AT);
        // Get all the posts
        query.findInBackground(new FindCallback<Image>() {
            @Override
            public void done(List<Image> images, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                groupImages.addAll(images);
                gridAdapter.notifyDataSetChanged();
            }
        });
    }
    
    public void saveImage(File image, Group group,  List<Image> groupImages, GridAdapter adapter) {
        Image img = new Image();
        img.setGroup(group);
        img.setImage(new ParseFile(image));
        img.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving image", e);
                }
                groupImages.add(img);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void queryVideos(Group group, List<Video> videos, VideoAdapter adapter) {
        ParseQuery<Video> query = ParseQuery.getQuery(Video.class);
        query.include(Video.KEY_GROUP);
        query.whereEqualTo(Video.KEY_GROUP, group);
        query.findInBackground(new FindCallback<Video>() {
            @Override
            public void done(List<Video> groupVideos, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                videos.addAll(groupVideos);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void queryLinks(Group group, List<Link> groupLinks, LinksAdapter adapter) {
        ParseQuery<Link> query = ParseQuery.getQuery(Link.class);
        query.include(Link.KEY_GROUP);
        query.include(Link.KEY_USER);
        query.whereEqualTo(Video.KEY_GROUP, group);
        query.findInBackground(new FindCallback<Link>() {
            @Override
            public void done(List<Link> links, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting links");
                    return;
                }
                groupLinks.addAll(links);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void saveLink(String description, String url, Group group, Context context, DialogFragment dialogFragment) {
        Link link = new Link();
        link.setDescription(description);
        link.setGroup(group);
        link.setURL(url);
        link.setUser(ParseUser.getCurrentUser());
        link.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving link", e);
                }
                MainActivity activity = (MainActivity) context;
                Fragment fragment = new LinksFragment();;
                Bundle bundle = new Bundle();
                bundle.putParcelable("itemGroup", group);
                FragmentTransaction ft = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                fragment.setArguments(bundle);
                ft.replace(R.id.frame, fragment).commit();
                ft.addToBackStack(null);
                dialogFragment.dismiss();
            }
        });
    }
}

package com.example.fbuapp.managers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fbuapp.R;
import com.example.fbuapp.fragments.groupFragments.CreateGroupFragment;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.School;
import com.hootsuite.nachos.NachoTextView;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.Headers;

public class ZoomManager {

    public static final String TAG = "ZoomManager";

    // Create Zoom room and set password and meetingID
    public void generateZoomRoom(Context context, boolean isVirtual, String groupName, School school, Location meetingLocation,
                                 String description, String day, String time, ArrayList<String> topics, Map<String, ParseUser> map,
                                 NachoTextView nUsers, Fragment fragment) throws JSONException {

        StringBuffer meetingID = new StringBuffer(context.getString(R.string.empty_string));
        StringBuffer password = new StringBuffer(context.getString(R.string.empty_string));
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        RequestHeaders headers = new RequestHeaders();
        params.put("userId", "anushkamehta311@gmail.com");
        params.put("type", 3);
        headers.put("Authorization", "Bearer " + context.getString(R.string.jwt_token));
        headers.put("Content-Type", "application/json");
        // Must create the request body
        String body = new StringBuilder().append("{\n").append("    \"topic\": \"general meeting\",\n").append("    \"type\": \"3\",\n").append("    \"settings\": {\n").append("        \"join_before_host\": \"true\"\n").append("    }\n").append("}").toString();
        client.post(context.getString(R.string.endPoint), headers, params, body, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JsonHttpResponseHandler.JSON json) {
                Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
                JSONObject jsonObject = json.jsonObject;
                // Get meeting id and password and store
                long id = 0;
                try {
                    id = jsonObject.getLong("id");
                    String meeting = String.valueOf(id);
                    meetingID.append(meeting);
                    Log.i(TAG, "meetingID: "+ meeting);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String pwd = jsonObject.getString("password");
                    // group.setPassword(pwd);
                    Log.i(TAG, "Password: " + pwd);
                    password.append(pwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // After zoom thread has finished, that's when we want to instantiate the new group
                Group group = new Group();
                group.setMeetingID(meetingID.toString());
                group.setPassword(password.toString());
                GroupManager groupManager = new GroupManager();
                try {
                    groupManager.createGroup(group, isVirtual, groupName, school, meetingLocation, description, day, time,
                            topics, map, nUsers, fragment, context);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse, @Nullable Throwable throwable) {
                Toast.makeText(context, errorResponse, Toast.LENGTH_LONG).show();
            }
        });
    }

}

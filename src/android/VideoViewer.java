package com.ccervantesb.videoviewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class echoes a string called from JavaScript.
 */
public class VideoViewer extends CordovaPlugin {

    private static final String TAG = "VideoViewer";
    private static final String STR_ERR_MISSING_PARAMS = "Missing params.";
    private static final String STR_ERR_INVALID_SRC = "Invalid file src.";
    private static final String STR_ERR_NOT_PERMISSIONS = "Permissions not granted.";
    private static final int INT_ERR_MISSING_PARAMS = -1;
    private static final int INT_ERR_INVALID_SRC = 5;
    private static final int INT_ERR_NOT_PERMISSIONS = 20;
    private static final int INT_REQ_SHOW_VIDEO = 1;
    private CallbackContext callbackContext;
    private static final String[] startURI = {
        "file",
        "http",
        "https",
        "content",
    };
    private String src;             // video src
    private String title;           // custom video title
    private boolean share = true;   // visibility share button


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        if (action.equals("show")) {
            if (!args.isNull(0)) {
                JSONObject params = args.getJSONObject(0);
                if (params.opt("src") != null) {
                    this.src = params.getString("src");
                }
                if (params.opt("title") != null) {
                    this.title = params.getString("title");
                }
                if (params.opt("share") != null) {
                    this.share = params.getBoolean("share");
                }
                this.showVideo(src, title, share);
            }
            else {
                handleError(INT_ERR_MISSING_PARAMS, STR_ERR_MISSING_PARAMS);
            }
            return true;
        }
        return false;
    }

    private void showVideo(String src, String title, boolean share) {
        String[] permissions = getPermissions();
        if (hasPermissions(permissions)) {
            Log.d(TAG, "All permissions granted.");
            if (isValidSrc(src)) {
                Log.d(TAG, "show video: " + src);
                Intent videoViewer = new Intent(cordova.getActivity(), VideoActivity.class);
                videoViewer.putExtra("src", src);
                videoViewer.putExtra("title", title);
                videoViewer.putExtra("share", share);
                cordova.getActivity().startActivity(videoViewer);
                PluginResult result = new PluginResult(PluginResult.Status.OK, "OK");
                callbackContext.sendPluginResult(result);
            }
            else {
                handleError(INT_ERR_INVALID_SRC, STR_ERR_INVALID_SRC);
            }
        }
        else {
            Log.d(TAG, "Request permissions,,,");
            requestPermissions(INT_REQ_SHOW_VIDEO, permissions);
        }
    }

    private boolean hasPermissions(String[] permissions) {
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if(!PermissionHelper.hasPermission(this, permission)){
                return false;
            }
        }
        return allPermissionsGranted;
    }

    private void requestPermissions(int requestCode, String[] permissions) {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }

    private String[] getPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return permissions.toArray(new String[0]);
    }

    private boolean isValidSrc(String src) {
        String start = src.split("://")[0];
        return Arrays.asList(startURI).contains(start);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case INT_REQ_SHOW_VIDEO:
                    showVideo(src, title, share);
                    break;
            }
        }
        else {
            handleError(INT_ERR_NOT_PERMISSIONS, STR_ERR_NOT_PERMISSIONS);
        }
    }

    private void handleError(int code, String message) {
        Log.d(TAG, "An error occurred: " + message);
        JSONObject error = new JSONObject();
        try {
            error.put("code", code);
            error.put("message", message);
        }
        catch(Exception e) {
            Log.d(TAG, e.getMessage());
        }
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, error);
        callbackContext.sendPluginResult(result);
    }
}
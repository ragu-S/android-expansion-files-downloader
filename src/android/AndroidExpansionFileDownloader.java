package com.androidexpansion.filedownloader;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by intricus on 16-07-01.
 */
public class AndroidExpansionFileDownloader extends CordovaPlugin {
    CallbackContext onProgressCallbackContext;
    CallbackContext downloadStatusContext;
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if("startDownload".equals(action) || !(args.get(0) instanceof JSONArray)) {
            if(args.length() < 2) {
                callbackContext.error("Failed to provide a valid array of asset urls or host url");
                return true;
            }
            final String hostUrl = (String) args.get(0);
            final JSONArray urls = (JSONArray) args.get(1);
            downloadStatusContext = callbackContext;
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {

                    startDownload(hostUrl, urls);
                }
            });
            return true;
        }
        else if ("onProgressCallback".equals(action)) {
            onProgressCallbackContext = callbackContext;
            return true;
        }

        return false;  // Returning false results in a "MethodNotFound" error.
    }
    public void onProgress(final Integer progressValue) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                onProgressCallbackContext.success(progressValue); // Thread-safe.
            }
        });
    }
    public void startDownload(String hostUrl, JSONArray jsonUrlArray) {
//        String hostUrl = "http://wwwqa.customizerassets.ford.com/v/1c3da8e0-3a4b-11e6-89f3-c7840396db3a";
        String fileUrl = "/cars/mustang2015/geometry/Geometry_305_v6_fb_coupe_frontlightbulb1_a001.b3d.dflr";
        String fileUrl2 = "/spritesheets/gameplayUi.json";
        DownloadExtraAssets downloadExtraAssets;
        DownloadFilesTask downloadFilesTask;
        String[] urls;
//        String[] urls = new String[] {
//                hostUrl + fileUrl2,
//        };
//        urls = jsonUrlArray.
        // Manages creating urls and saving files in specific locations
        // Host URL: must include full path to assets folder, dev server uses mustangdev.jam3.net/assets/
        downloadExtraAssets = new DownloadExtraAssets(jsonUrlArray, hostUrl, cordova.getActivity().getApplicationContext());

        // Manages downloading files asynchronously
        downloadFilesTask = new DownloadFilesTask(downloadExtraAssets, this);

        downloadFilesTask.execute(downloadExtraAssets.localRemoteResources);
    }

}

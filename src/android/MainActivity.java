package com.androidexpansion.filedownloader;

import android.os.Bundle;

import android.util.Log;

import org.apache.cordova.CordovaActivity;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends CordovaActivity {
    DownloadExtraAssets downloadExtraAssets;
    DownloadFilesTask downloadFilesTask;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        setUpFileDownloader();
        super.onCreate(savedInstanceState);
        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
    }
    public JSONArray loadJson() {
//        String jsonFileUri = R.raw.expansion_assets; //R.
        try {

            InputStream input = getResources().openRawResource(R.raw.expansion_assets);
            InputStreamReader inputStreamReader = new InputStreamReader(input, "UTF-8");

            BufferedReader reader = new BufferedReader(
                    inputStreamReader, 1024);
            StringBuilder builder = new StringBuilder();
            String jsonString;

            while((jsonString = reader.readLine()) != null) {
                builder.append(jsonString);
            }
            JSONArray assetUrls = new JSONArray(builder.toString());
            return assetUrls;
        }
        catch(Exception e) {
            return null;
        }
    }
    public void setUpFileDownloader() {

        String hostUrl = "http://wwwqa.customizerassets.ford.com/v/1c3da8e0-3a4b-11e6-89f3-c7840396db3a";
        String fileUrl = "/cars/mustang2015/geometry/Geometry_305_v6_fb_coupe_frontlightbulb1_a001.b3d.dflr";
        String fileUrl2 = "/spritesheets/gameplayUi.json";
        // " data/data/com.mustangcustomizer2.alpha/files/www/assets/cars/mustang2015/geometry/Geometry_305_v6_fb_coupe_frontlightbulb1_a001.b3d.dflr";
        JSONArray urlList = loadJson();
        List<String> list = new ArrayList<String>();
        try {
            for (int i = 0; i < urlList.length(); i++) {
                list.add(hostUrl + "/" + urlList.getString(i));
            }
        }
        catch (JSONException e) {
            Log.e("JSON_Error", e.getMessage());
        }
//        urlList.
        String[] urls = new String[list.size()];
//        String[] urls = new String[] {
//                hostUrl + fileUrl2,
//        };
//        urls = list.toArray(urls);
        // Manages creating urls and saving files in specific locations
        // Host URL: must include full path to assets folder, dev server uses mustangdev.jam3.net/assets/
//        downloadExtraAssets = new DownloadExtraAssets(urls, hostUrl, this.getApplicationContext());
//
//        // Manages downloading files asynchronously
//        downloadFilesTask = new DownloadFilesTask(downloadExtraAssets, this);
//
//        downloadFilesTask.execute(downloadExtraAssets.localRemoteResources);
    }
}

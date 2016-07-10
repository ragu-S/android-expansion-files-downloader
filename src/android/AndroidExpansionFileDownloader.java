package com.androidexpansion.filedownloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/**
 * Created by intricus on 16-07-01.
 */
public class AndroidExpansionFileDownloader extends CordovaPlugin {
    CallbackContext downloadStatusContext;
    ProgressDialog progress;
    JSONObject fileListing;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Context context = cordova.getActivity().getApplicationContext();
        String appDirectoryPath = context.getFilesDir().getAbsolutePath();
        Log.d("ASSET_FOLDER_DEBUG", appDirectoryPath);
        fileListing = new JSONObject();
        getAllFiles(new File(appDirectoryPath), fileListing);

        JSONArray jsonResult = new JSONArray();
        File jsonFile = new File(Environment.getExternalStorageDirectory(), "FILE_LISTING.json");
        try {
            FileOutputStream outputStream = new FileOutputStream(jsonFile);
            OutputStreamWriter OutDataWriter  = new OutputStreamWriter(outputStream);

            OutDataWriter.write(fileListing.toString(3));

            OutDataWriter.close();
            outputStream.close();
        }
        catch(Exception e) {
            Log.e("FILE LISTING ERROR", "Failed to write json file");
        }
    }
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if("startDownload".equals(action) || args.length() > 0 && !(args.get(0) instanceof JSONArray)) {
            if(args.length() < 2) {
                callbackContext.error("Failed to provide a valid array of asset urls or host url");
            }
            else {
                final String hostUrl = (String) args.get(0);
                final JSONArray urls = (JSONArray) args.get(1);
                downloadStatusContext = callbackContext;
                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        startDownload(hostUrl, urls);
                    }
                });
            }
        }
        else if ("checkIfFilesDownloadedPrior".equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    JSONArray result = DownloadExtraAssets.checkIfFilesWereDownloadedPrior(cordova.getActivity().getApplicationContext());
                    if(result == null)
                        callbackContext.success(0);
                    else
                        callbackContext.success(result);
                }
            });
        }
        else if ("downloadZipFile".equals(action)) {

        }
        else if ("getFileListing".equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    PluginResult pr = new PluginResult(PluginResult.Status.OK, fileListing);
                    callbackContext.sendPluginResult(pr);
                }
            });
        }

        return true;  // Returning false results in a "MethodNotFound" error.
    }
    public void createProgressDialog() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                progress = new ProgressDialog(cordova.getActivity(), ProgressDialog.STYLE_HORIZONTAL);
                progress.setTitle("Expansion Files");
                progress.setMessage("Downloading extra assets");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setCancelable(false);
                progress.show();
            }
        });
    }
    public void startDownload(String hostUrl, JSONArray jsonUrlArray) {
//        String hostUrl = "http://wwwqa.customizerassets.ford.com/v/1c3da8e0-3a4b-11e6-89f3-c7840396db3a";
//        String fileUrl = "/cars/mustang2015/geometry/Geometry_305_v6_fb_coupe_frontlightbulb1_a001.b3d.dflr";
//        String fileUrl2 = "/spritesheets/gameplayUi.json";

        DownloadExtraAssets downloadExtraAssets;
        DownloadFilesTask downloadFilesTask;

        // Manages creating urls and saving files in specific locations
        // Host URL: must include full path to assets folder, dev server uses mustangdev.jam3.net/assets/
        downloadExtraAssets = new DownloadExtraAssets(jsonUrlArray, hostUrl, cordova.getActivity().getApplicationContext());

        // Manages downloading files asynchronously
        downloadFilesTask = new DownloadFilesTask(downloadExtraAssets, this);


        downloadFilesTask.execute(downloadExtraAssets.localRemoteResources);
    }
    private static JSONObject getAllFiles(File curDir, JSONObject filesInCurrentDirectory) {
//        JSONObject filesInCurrentDirectory = new JSONObject();
        File[] filesList = curDir.listFiles();
        JSONArray fileListing = new JSONArray();
        for(File f : filesList){
            try {
                if (f.isDirectory()) {
                    getAllFiles(f, filesInCurrentDirectory);
                }
                if (f.isFile()) {
                    filesInCurrentDirectory.put(f.getName(), f.getAbsolutePath());
                }
            }
            catch(Exception e) {
                Log.e("JSON_FILE_LIST_ERROR", "unable to list file name for " + f.getName());
            }
        }
        try {
            filesInCurrentDirectory.put(curDir.getName(), fileListing);
        }
        catch(Exception e) {
            Log.e("JSON_FILE_LIST_ERROR", "unable to list file name for " + curDir.getName());
        }
        return filesInCurrentDirectory;
    }

    public void readZipFile(File zipFile) {
        try {
            if(zipFile == null || !zipFile.isFile()) {
                Log.e("ZIP_READ_ERROR", "unable to find zip file");
                return;
            }

            ZipFile zip = new ZipFile(zipFile);
            for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
                ZipEntry entry = e.nextElement();

                Log.d("ZIP_ENTRY", entry.getName());
                zip.getInputStream(entry);
                //entry.
                //System.out.println(e.nextElement());
            }
        }
        catch(Exception e) {
            Log.e("ZIP_READ_ERROR", e.getMessage());
        }

    }
}

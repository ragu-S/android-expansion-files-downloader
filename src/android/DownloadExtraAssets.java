package com.androidexpansion.filedownloader;

/**
 * Created by intricus on 16-07-01.
 */
import android.content.Context;
import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;

import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadExtraAssets {
    public String assetFolderPath;
    public LocalRemoteAssetResource[] localRemoteResources;
    public static String hostUrl;
    public JSONArray assetsThatFailedToDownload;

    public class LocalRemoteAssetResource {
        public String fileName;
        public String subDirectory;
        public URL remoteUrl;

        public LocalRemoteAssetResource(String remoteUrlString) throws MalformedURLException {
            this.remoteUrl = new URL(DownloadExtraAssets.hostUrl + "/" + remoteUrlString);
            parseUrlsPaths(remoteUrlString);
        }
        private void parseUrlsPaths(String localPath) {
            // /cars/mustang2015/geometry/Geometry_305_v6_fb_coupe_frontlightbulb1_a001.b3d.dflr
            // Set local uri path for subdirectory
            String[] pathArray = localPath.split("/");
            // Geometry_305_v6_fb_coupe_frontlightbulb1_a001.b3d.dflr
            this.fileName = pathArray[pathArray.length - 1];
            // /cars/mustang2015/geometry/
            this.subDirectory = localPath.replace("/" + this.fileName, "");
        }
    }

    // String contentEncoding = "UTF-8";
    public DownloadExtraAssets(JSONArray urls, String hostUrl, Context context) {
        DownloadExtraAssets.hostUrl = hostUrl;
        String packageName =  context.getPackageName();

        // Get path to where assets need to be stored
        // QA/Prod: data/data/com.mustangcustomizer2.alpha/files/www/assets/cars/mustang2015/geometry/Geometry_305_v6_fb_coupe_frontlightbulb1_a001.b3d.dflr";
        // /data/user/0/com.mustangcustomizer2.alpha/files/
        // /data/user/0/com.mustangcustomizer2.alpha/files/
        this.assetFolderPath = Environment.getDataDirectory().getAbsolutePath() + File.separator  + "user"
                + File.separator + "0"
                + File.separator + packageName + File.separator + "files"
                + File.separator + "www" + File.separator + "assets";

        this.localRemoteResources = createLocalRemoteUris(urls);
        this.assetsThatFailedToDownload = new JSONArray();

    }
    public static JSONArray checkIfFilesWereDownloadedPrior(Context context) {
        String packageName =  context.getPackageName();
        String assetFolderPath = Environment.getDataDirectory().getAbsolutePath() + File.separator  + "data"
                + File.separator + packageName + File.separator + "files"
                + File.separator + "www" + File.separator + "assets";
        File filesDownloaded = new File(assetFolderPath, "FilesDownloaded.json");

        JSONArray jsonResult = new JSONArray();
        if(!filesDownloaded.isFile()) {
            return null;
        }
        try {
            FileReader fileread = new FileReader(filesDownloaded);
            JsonReader reader = new JsonReader(fileread);
            reader.beginArray();

            while (reader.hasNext()) {
                jsonResult.put(reader.nextString());
            }

            reader.endArray();
            reader.close();
            fileread.close();
        }
        catch(Exception e) {
            Log.e("JSON_READ_ERROR ", e.getMessage());
        }
        return jsonResult;
    }
    public void saveFileDownloadResults() {
        File filesDownloaded = new File(this.assetFolderPath, "FilesDownloaded.json");

        try {
            FileOutputStream fileWriter = new FileOutputStream(filesDownloaded);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fileWriter, "UTF-8"));
            writer.setIndent("  ");
            writer.beginArray();
            for(int i = 0; i < this.assetsThatFailedToDownload.length(); i++) writer.value((String) this.assetsThatFailedToDownload.get(i));
            writer.endArray();
            writer.close();
            fileWriter.close();
        }
        catch(Exception e) {
            Log.e("JSON_WRITE_ERROR", e.getMessage());
        }
    }
    public LocalRemoteAssetResource[] createLocalRemoteUris(JSONArray urls) {
        LocalRemoteAssetResource[] localRemoteAssetResources = new LocalRemoteAssetResource[urls.length()];
        int totalUrls = urls.length();
        for (int i = 0; i < totalUrls; i++) {
            // Create resource objects
            try {
                localRemoteAssetResources[i] = new LocalRemoteAssetResource((String) urls.get(i));
            }
            catch (MalformedURLException e) {
                try {
                    assetsThatFailedToDownload.put(urls.get(i));
                    Log.e("MalformedURLException", e.getMessage());
                }
                catch(JSONException jsonException) {
                    Log.e("JSONExceptions", jsonException.getMessage());
                    assetsThatFailedToDownload.put("unknown url at " + i);
                }
            }
            catch(JSONException e) {
                Log.e("JSONExceptions", e.getMessage());
                assetsThatFailedToDownload.put("unknown url at " + i);
            }
        }
        return localRemoteAssetResources;
    }
    public long downloadFile(LocalRemoteAssetResource localRemoteResource) {
        try {
            // Get/Create subdirectory the file belongs to
            File subDirectory = createSubDirectories(localRemoteResource.subDirectory);

            // Get File reference
            File assetFile = new File(subDirectory, localRemoteResource.fileName);

            // Check if file exits already
            if(assetFile.isFile()) {
                return assetFile.length();
            }

            // Prepare to download file from Server
            URL url = localRemoteResource.remoteUrl;
            url.openConnection();
            InputStream inputStream = new BufferedInputStream(url.openStream(), 10250);
            FileOutputStream outputStream = new FileOutputStream(assetFile);
            byte buffer[] = new byte[1024];
            int dataSize;
            long loadedSize = 0;
            while ((dataSize = inputStream.read(buffer)) != -1) {
                loadedSize += dataSize;
                outputStream.write(buffer, 0, dataSize);
            }
            inputStream.close();
            outputStream.close();

            return loadedSize;
        }
        catch(Exception exp) {
            Log.e("ASSET_ERROR", exp.getMessage());
            assetsThatFailedToDownload.put(localRemoteResource.remoteUrl.toString());
            return 0;
        }
    }
    public File createSubDirectories(String directoryPath) {
        // Uses the right asset folder path
        File myFilesDir = new File(this.assetFolderPath, directoryPath);

        if(!myFilesDir.isDirectory()) {
            myFilesDir.mkdirs();
        }
        return myFilesDir;
    }
}

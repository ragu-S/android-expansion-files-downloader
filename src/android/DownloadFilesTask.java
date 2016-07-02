package com.androidexpansion.filedownloader;

/**
 * Created by intricus on 16-07-01.
 */
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by intricus on 16-07-01.
 */
public class DownloadFilesTask extends AsyncTask<DownloadExtraAssets.LocalRemoteAssetResource, Integer, Long> {
    DownloadExtraAssets downloadExtraAssets;
    AndroidExpansionFileDownloader reference;
    public DownloadFilesTask(DownloadExtraAssets downloadExtraAssets, AndroidExpansionFileDownloader reference) {
        this.downloadExtraAssets = downloadExtraAssets;
        this.reference = reference;
    }
    protected Long doInBackground(DownloadExtraAssets.LocalRemoteAssetResource[] remoteLocalResources) {
        int count = remoteLocalResources.length;
        long totalSize = 0;
        for (int i = count-1; i >= 0; i--) {
            totalSize += downloadExtraAssets.downloadFile(remoteLocalResources[i]);
            Integer progress = (int)((i / (float) count) * 100);

            publishProgress(progress);

            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return totalSize;
    }
    protected void onProgressUpdate(Integer... progress) {
        Log.d("Progress: ", progress[0].toString());

        // Publish progress back to Javascript context via Cordova CallbackContext
        this.reference.onProgress(progress[0]);
    }
    protected void onPostExecute(Long result) {
        Log.d("DOWNLOAD_FINISHED", "Downloaded " + result + " bytes");

        if(this.reference.downloadStatusContext != null) {
            this.reference.downloadStatusContext.success("Download finished!");
        }
    }
}
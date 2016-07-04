package com.androidexpansion.filedownloader;

/**
 * Created by intricus on 16-07-01.
 */
import android.os.AsyncTask;
import android.util.Log;

import org.apache.cordova.PluginResult;

public class DownloadFilesTask extends AsyncTask<DownloadExtraAssets.LocalRemoteAssetResource, Integer, Long> {
    DownloadExtraAssets downloadExtraAssets;
    AndroidExpansionFileDownloader reference;

    public DownloadFilesTask(DownloadExtraAssets downloadExtraAssets, AndroidExpansionFileDownloader reference) {
        this.downloadExtraAssets = downloadExtraAssets;
        this.reference = reference;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.reference.createProgressDialog();
    }
    protected Long doInBackground(DownloadExtraAssets.LocalRemoteAssetResource[] remoteLocalResources) {
        int count = remoteLocalResources.length;
        long totalSize = 0;
        for (int i = 0; i < count; i++) {
            totalSize += downloadExtraAssets.downloadFile(remoteLocalResources[i]);
            Integer progress = (int)(((i+1) / (float) count) * 100);

            publishProgress(progress);

            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return totalSize;
    }
    protected void onProgressUpdate(Integer... progress) {
        Log.d("Progress: ", progress[0].toString());

        this.reference.progress.setProgress(progress[0]);

    }
    protected void onPostExecute(Long result) {
        Log.d("DOWNLOAD_FINISHED", "Downloaded " + result + " bytes");
        if(this.reference.progress != null) {
            this.reference.progress.dismiss();
        }
        // Save a json with missing files in assets folder
        this.downloadExtraAssets.saveFileDownloadResults();

        // Return result back to Javascript context
        if(this.reference.downloadStatusContext != null) {
            PluginResult pr = new PluginResult(PluginResult.Status.OK, this.downloadExtraAssets.assetsThatFailedToDownload);
            this.reference.downloadStatusContext.sendPluginResult(pr);
        }
    }
}

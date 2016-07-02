module.exports = {
    startDownload: function(hostUrl, assetUrls, successcallback, errorcallback) {
        cordova.exec(
            successcallback,
            errorcallback,
            'AndroidExpansionFilesDownloader',
            'startDownload',
            [hostUrl, assetUrls]
        );
    },
    onProgressCallback: function(successcallback, errorcallback) {
        cordova.exec(
            successcallback,
            errorcallback,
            'AndroidExpansionFilesDownloader',
            'onProgressCallback',
            []
        );
    }
}

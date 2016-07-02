module.exports = {
    startDownload: function(hostUrl, assetUrls, successcallback, errorcallback) {
        cordova.exec(
            successcallback,
            errorcallback,
            'AndroidExpansionFileDownloader',
            'startDownload',
            [hostUrl, assetUrls]
        );
    },
    onProgressCallback: function(successcallback, errorcallback) {
        cordova.exec(
            successcallback,
            errorcallback,
            'AndroidExpansionFileDownloader',
            'onProgressCallback',
            []
        );
    }
}

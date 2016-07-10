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
    checkIfFilesDownloadedPrior: function(successcallback, errorcallback) {
        cordova.exec(
            successcallback,
            errorcallback,
            'AndroidExpansionFileDownloader',
            'checkIfFilesDownloadedPrior',
            []
        );
    },
    getFileListing : function(successcallback, errorcallback) {
        cordova.exec(
            successcallback,
            errorcallback,
            'AndroidExpansionFileDownloader',
            'getFileListing',
            []
        );
    }
}

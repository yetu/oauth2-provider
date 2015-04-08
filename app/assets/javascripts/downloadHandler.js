window.DownloadHandler = {};
(function(DownloadHandler){
  DownloadHandler.onClickDownload = function(){
    document.getElementById('fullContainer').setAttribute('style', 'display:block;');
    document.getElementById('leftContainer').setAttribute('style', 'display:none;');
    document.getElementById('rightContainer').setAttribute('style', 'display:none;');
  };
})(window.DownloadHandler);
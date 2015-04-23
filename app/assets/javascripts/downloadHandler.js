window.DownloadHandler = {};
(function(DownloadHandler){
  DownloadHandler.onClickDownload = function(){
    document.getElementById('fullContainer_'+window.DeterminedOS.name).setAttribute('class', 'full show');
    document.getElementById('content').setAttribute('class', 'content content__full');
    document.getElementById('leftContainer').setAttribute('class', 'left hide');
    document.getElementById('rightContainer').setAttribute('class', 'right hide');
  };
})(window.DownloadHandler);
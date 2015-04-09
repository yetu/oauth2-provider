/**
 * Created by elisahilprecht on 09/04/15.
 */
(function(){
  var clickOnErrorMessage = function(e) {
    e.toElement.setAttribute('class','help-inline display-none');
    document.getElementById(e.toElement.id.replace('ErrorText','')).focus();
  };

  var clickOnInputField = function(e){
    document.getElementById(e.toElement.id+'ErrorText').setAttribute('class','help-inline display-none');
  };

  var helpInlines = document.getElementsByClassName('help-inline');
  for(var i=0; i<helpInlines.length; i++){
    var ele = helpInlines[i];
    if(ele.getAttribute('class')==='help-inline'){
      ele.onclick = clickOnErrorMessage;
      var inputId = ele.id.replace('ErrorText', '');
      var inputValue = document.getElementById(inputId).value;
      if(inputValue!==undefined && inputValue!==''){
        ele.setAttribute('class', 'help-inline help-inline__bottom' )
      }
    }
  }
  var inputFields = document.getElementsByTagName('input');
  for(var j=0; j<inputFields.length; j++){
    ele.onclick = clickOnInputField;
  }
})();

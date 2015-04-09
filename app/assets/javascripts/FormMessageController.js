/**
 * Created by elisahilprecht on 09/04/15.
 */
window.FormMessageController = {};
(function(FormMessageController){

  var getElementsByClassName = function(node, classname) {
    var a = [];
    var re = new RegExp('(^| )'+classname+'( |$)');
    var els = node.getElementsByTagName("*");
    for(var i=0,j=els.length; i<j; i++)
      if(re.test(els[i].className))a.push(els[i]);
    return a;
  }

  FormMessageController.clickOnErrorMessage = function(ele) {
    if(ele.id.indexOf('agreement')===-1){
      ele.setAttribute('class','help-inline display-none');
      document.getElementById(ele.id.replace('ErrorText','')).focus();
    }
  };

  FormMessageController.clickOnInputField = function(e){
    if(e.toElement.id.indexOf('agreement')>-1){
      e.toElement.id = 'agreement';
    }
    document.getElementById(e.toElement.id+'ErrorText').setAttribute('class','help-inline display-none');
  };

  var helpInlines = getElementsByClassName(document.getElementById('signup_form'), 'help-inline');
  for(var i=0; i<helpInlines.length; i++){
    var ele = helpInlines[i];
    if(ele.getAttribute('class')==='help-inline'){
      var inputId = ele.id.replace('ErrorText', '');
      var inputValue = document.getElementById(inputId).value;
      if(inputValue!==undefined && inputValue!==''){
        ele.setAttribute('class', 'help-inline help-inline__bottom' )
      }
    }
  }
  var inputFields = document.getElementsByTagName('input');
  for(var j=0; j<inputFields.length; j++){
    inputFields[j].onclick = FormMessageController.clickOnInputField;
  }
})(window.FormMessageController);

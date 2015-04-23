/**
 * Created by elisahilprecht on 09/04/15.
 */
window.formMessageController = {};
(function(FormMessageController){

  var getElementsByClassName = function(node, classname) {
    var a = [];
    var re = new RegExp('(^| )'+classname+'( |$)');
    var els = node.getElementsByTagName("*");
    for(var i=0,j=els.length; i<j; i++)
      if(re.test(els[i].className))a.push(els[i]);
    return a;
  };

  FormMessageController.clickOnErrorMessage = function(ele) {
    if(ele.id.indexOf('agreement')===-1){
      ele.setAttribute('class','help-inline display-none');
      document.getElementById(ele.id.replace('ErrorText','')).focus();
    }
  };

  FormMessageController.clickOnInputField = function(e){
    if(e){
      var element = e.srcElement ? e.srcElement : e.toElement;
      element = element ? element : e.target;
      var errorText = document.getElementById(element.id.replace('_agreement', '')+'ErrorText');
      if(errorText){
        errorText.setAttribute('class','help-inline display-none');
      }
    }
  };

  FormMessageController.hoverHelp = function(element){
    element.setAttribute('class', 'help-block');
  };
  FormMessageController.blurHelp = function(element){
    element.setAttribute('class', 'help-block display-none');
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
    var inputField = inputFields[j];
    inputField.onclick = FormMessageController.clickOnInputField;
    inputField.onfocus = FormMessageController.clickOnInputField;
    var errorElement = document.getElementById(inputField.id+'ErrorText');
    if(errorElement!=undefined){
      inputField.placeholder = '';
    }

  }

  //Set custom radio buttons as span and manage interaction for them
  var radioButtonSet = document.getElementById('UserRegistrationStatus');
  var radioButtonNotRegistered =  document.getElementById('UserRegistrationStatus_UserNotRegistered');
  var radioButtonRegistered =  document.getElementById('UserRegistrationStatus_UserAlreadyRegistered');

  var customRadioButtonNotRegistered = document.createElement('span');
  customRadioButtonNotRegistered.setAttribute('class','radio radio__checked');
  var customRadioButtonRegistered = document.createElement('span');
  customRadioButtonRegistered.setAttribute('class','radio radio__registered');

  radioButtonSet.insertBefore(customRadioButtonNotRegistered, radioButtonNotRegistered);
  radioButtonSet.insertBefore(customRadioButtonRegistered, radioButtonRegistered);

  customRadioButtonNotRegistered.onclick = function(){
    radioButtonNotRegistered.checked = true;
    customRadioButtonRegistered.setAttribute('class', 'radio radio__registered');
    customRadioButtonNotRegistered.setAttribute('class','radio radio__checked');
  };

  customRadioButtonRegistered.onclick = function(){
    radioButtonRegistered.checked = true;
    customRadioButtonNotRegistered.setAttribute('class', 'radio');
    customRadioButtonRegistered.setAttribute('class','radio radio__registered radio__checked');
  };

  var createXMLHttpRequest = function(){
    var xmlhttp;
    if (window.XMLHttpRequest)
    {// code for IE7+, Firefox, Chrome, Opera, Safari
      xmlhttp=new XMLHttpRequest();
    }
    else
    {// code for IE6, IE5
      xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    return xmlhttp
  };

  var passwordInput = document.getElementById("password1ID");
  var setErrorState = function(){
    document.getElementById("password1IDHelpIcon").setAttribute("class", "help-icon failure-password");
    passwordInput.setAttribute("class", "left__input failure-password");
  };
  var setNormalState = function(){
    document.getElementById("password1IDHelpIcon").setAttribute("class", "help-icon");
    passwordInput.setAttribute("class", "left__input");
  };
  var sendCheckRequest = function(password){
    var xmlhttp = createXMLHttpRequest();
    xmlhttp.open("POST","/setup/checkPassword",true);
    xmlhttp.onreadystatechange=function()
    {
      if (xmlhttp.readyState==4 && xmlhttp.status==200)
      {
        setNormalState();
      }
      else if(xmlhttp.readyState==4 && xmlhttp.status==400){
        setErrorState();
      }
    };
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("password="+password);
  };
  passwordInput.onkeyup = function(){
    var password = passwordInput.value;
    if(password===""){
      setNormalState();
    }
    else{
      sendCheckRequest(password);
    }
  }

})(window.formMessageController);

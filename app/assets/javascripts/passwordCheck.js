/**
 * Created by elisahilprecht on 23/04/15.
 */
(function(){
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
}());
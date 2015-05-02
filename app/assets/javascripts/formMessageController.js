/**
 * Created by elisahilprecht on 09/04/15.
 */
var FormMessageController = function(passwordFeedbackIsShown){
  console.log(passwordFeedbackIsShown);
  var helpIconLabel = 'HelpIcon';
  var helpTextLabel = 'HelpText';
  var errorTextLabel = 'ErrorText';
  var passwordId = 'password1ID';

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

  var enterInputField = function(e){
    if(e){
      var element = getElementFromEvent(e);
      //remove error text, if some is there
      var errorText = document.getElementById(element.id.replace('_agreement', '')+'ErrorText');
      if(errorText){
        errorText.setAttribute('class','help-inline display-none');
      }

      //show the help text
      document.getElementById(element.id.replace('_agreement', '')+'HelpText')
        .setAttribute("class", "help-block display");
    }
  };
  var leaveInputField = function(e){
    if(e) {
      var inputField = getElementFromEvent(e);
      //hide help text
      if(!(inputField.id.indexOf(passwordId)>-1&&passwordFeedbackIsShown)) {
        document.getElementById(inputField.id.replace('_agreement', '') + 'HelpText')
          .setAttribute('class', 'help-block display-none');
      }
    }
  };

  var getElementFromEvent = function(e){
    var element = e.srcElement ? e.srcElement : e.toElement;
    element = element ? element : e.target;
    return element;
  };

  var hoverHelp = function(e){
    if(e) {
      var helpIcon = getElementFromEvent(e);
      var helpText = document.getElementById(helpIcon.id.replace(helpIconLabel,helpTextLabel));
      helpText.setAttribute('class', 'help-block');
    }
  };

  var blurHelp = function(e){
    if(e) {
      var helpIcon = getElementFromEvent(e);
      if(!(helpIcon.id.indexOf(passwordId)>-1&&passwordFeedbackIsShown)){
        var helpText = document.getElementById(helpIcon.id.replace(helpIconLabel,helpTextLabel));
        helpText.setAttribute('class', 'help-block display-none');
      }
    }
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

  var helpIcons = getElementsByClassName(document.getElementById('signup_form'), 'help-icon');
  for(var l=0; l<helpIcons.length; l++){
    var helpIcon = helpIcons[l];
    helpIcon.onmouseover=hoverHelp;
    helpIcon.onmouseout=blurHelp;
  }

  var inputFields = document.getElementsByTagName('input');
  for(var j=0; j<inputFields.length; j++){
    var inputField = inputFields[j];
    inputField.onclick = enterInputField;
    inputField.onfocus = enterInputField;
    inputField.onblur = leaveInputField;

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

};

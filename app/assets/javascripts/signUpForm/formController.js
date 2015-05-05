//TODO: when text empty on password field hide help when leaving field
//TODO: passwordMatch error message
//TODO: stages for password strength on password help Text feedback
//TODO: JS code refactoring

var FormController = function(validators){
  var helpIconLabel = 'HelpIcon';
  var helpTextLabel = 'HelpText';
  var errorTextLabel = 'ErrorText';
  var agreementId = 'agreement';
  var signUpFormId = 'signup_form';

  var getElementsByClassName = function(node, classname) {
    var a = [];
    var re = new RegExp('(^| )'+classname+'( |$)');
    var els = node.getElementsByTagName('*');
    for(var i=0,j=els.length; i<j; i++)
      if(re.test(els[i].className))a.push(els[i]);
    return a;
  };

  var clickOnErrorMessage = function(e) {
    var errorText = getElementFromEvent(e);
    if(errorText.id.indexOf(agreementId)===-1){
      errorText.setAttribute('class','help-inline display-none');
      var inputField = document.getElementById(errorText.id.replace(errorTextLabel,''));
      inputField.focus();
    }
  };

  var enterInputField = function(e){
    if(e){
      var inputField = getElementFromEvent(e);
      //remove error text, if some is there
      var errorText = document.getElementById(inputField.id+errorTextLabel);
      if(errorText){
        errorText.setAttribute('class','help-inline display-none');
      }

      //show the help text
      if(inputField.id.indexOf(agreementId)===-1){
          document.getElementById(inputField.id+'HelpText')
            .setAttribute('class', 'help-block display');
      }
    }
  };
  var leaveInputField = function(e){
    if(e) {
      var inputField = getElementFromEvent(e);
      //hide help text
      var helpText = document.getElementById(inputField.id + helpTextLabel);
      helpText.setAttribute('class', 'help-block display-none');
    }
  };

  var onChangeInputField = function(e) {
    if(e) {
      validateInputField(getElementFromEvent(e));
    }
  };

  var validateInputField = function(inputField){
    if (validators(inputField.id)) {
      inputField.classList.add('valid');
    } else {
      inputField.classList.remove('valid');
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
      var inputField = document.getElementById(helpIcon.id.replace(helpIconLabel,''));
      var activeElement = document.activeElement;
      if(inputField.id !== activeElement.id){
        var helpText = document.getElementById(helpIcon.id.replace(helpIconLabel,helpTextLabel));
        helpText.setAttribute('class', 'help-block display-none');
      }
    }
  };


  var setClickEventHandlerForErrorText = function(errorTextElement){
    errorTextElement.onclick = clickOnErrorMessage;
  };

  /**
   * Place error texts in input field, when there is no value in input field.
   * Otherwise they are place below the input field.
   */
  var placeErrorText = function(errorTextElement){
    var inputId = errorTextElement.id.replace('ErrorText', '');
    var inputValue = document.getElementById(inputId).value;
    if(inputValue!==undefined && inputValue!==''){
      errorTextElement.setAttribute('class', 'help-inline help-inline__bottom' )
    }
  };

  var initErrorTextElements = function () {
    var errorTextElements = getElementsByClassName(document.getElementById(signUpFormId), 'help-inline');
    //TODO: use not for-loop
    for (var i = 0; i < errorTextElements.length; i++) {
      var errorTextElement = errorTextElements[i];
      if (errorTextElement.getAttribute('class') === 'help-inline') {
        setClickEventHandlerForErrorText(errorTextElement);
        placeErrorText(errorTextElement);
      }
    }
  };

  var setEventHandlerForHelpIcons = function(){
    var helpIcons = getElementsByClassName(document.getElementById(signUpFormId), 'help-icon');
    //TODO: use not for-loop
    for(var l=0; l<helpIcons.length; l++){
      var helpIcon = helpIcons[l];
      helpIcon.onmouseover=hoverHelp;
      helpIcon.onmouseout=blurHelp;
    }
  };

  var setEventHandlerForInputField = function(inputField){
    inputField.onclick = enterInputField;
    inputField.onfocus = enterInputField;
    inputField.onblur = leaveInputField;
    inputField.onchange = onChangeInputField;
  };

  var removePlaceholderOnError = function(inputField){
    var errorTextElement = document.getElementById(inputField.id+errorTextLabel);
    if(errorTextElement!=undefined){
      inputField.placeholder = '';
    }
  };

  var initInputFields = function(){
    var inputFields = document.getElementsByTagName('input');
    //TODO: use not for-loop
    for(var j=0; j<inputFields.length; j++){
      var inputField = inputFields[j];
      setEventHandlerForInputField(inputField);
      removePlaceholderOnError(inputField);
      validateInputField(inputField);
    }
  };

  /**
   * Set custom radio buttons as span and manage interaction for them
   */
  var createCustomRadioButtons = function(){
    var radioButtonSet = document.getElementById('UserRegistrationStatus');
    var radioButtonNotRegistered =  document.getElementById('UserRegistrationStatus_UserNotRegistered');
    var radioButtonRegistered =  document.getElementById('UserRegistrationStatus_UserAlreadyRegistered');

    var customRadioButtonNotRegistered = document.createElement('span');
    customRadioButtonNotRegistered.setAttribute('class','radio radio__checked');
    var customRadioButtonRegistered = document.createElement('span');
    customRadioButtonRegistered.setAttribute('class','radio radio__registered');

    radioButtonSet.insertBefore(customRadioButtonNotRegistered, radioButtonNotRegistered);
    radioButtonSet.insertBefore(customRadioButtonRegistered, radioButtonRegistered);

    var customRadioButtonEventHandler = {
      clickOnNotRegisteredBtn: function(){
        radioButtonNotRegistered.checked = true;
        customRadioButtonRegistered.setAttribute('class', 'radio radio__registered');
        customRadioButtonNotRegistered.setAttribute('class','radio radio__checked');
      },
      clickOnRegisteredBtn: function(){
        radioButtonRegistered.checked = true;
        customRadioButtonNotRegistered.setAttribute('class', 'radio');
        customRadioButtonRegistered.setAttribute('class','radio radio__registered radio__checked');
      }
    };

    customRadioButtonNotRegistered.onclick = customRadioButtonEventHandler.clickOnNotRegisteredBtn;
    customRadioButtonRegistered.onclick =  customRadioButtonEventHandler.clickOnRegisteredBtn;
  };

  return {
    init: function(){
      createCustomRadioButtons();
      initInputFields();
      initErrorTextElements();
      setEventHandlerForHelpIcons();
    }
  }

};

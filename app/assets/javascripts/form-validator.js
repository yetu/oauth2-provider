var FormValidator = function(root) {
  this.init = function(root) {
    root = root || document;
    var inputs = root.querySelectorAll('input');
    for (var i=0; i < inputs.length; i++) {
      var input = inputs[i];
      // Initialize input's error
      var inputError = root.getElementById(input.id + 'ErrorText');
      if (inputError) {
        // Do not show placeholder if input has an error as error text will cover it
        input.placeholderCopy = input.placeholder;
        input.placeholder = '';
        inputError.addEventListener('click', this.onInputErrorClick.bind(this, input));
      }
      // Initialize input's help icon
      var inputHintIcon = root.getElementById(input.id + 'HelpIcon');
      var inputHintText = root.getElementById(input.id + 'HelpText');
      if (inputHintIcon && inputHintText) {
        inputHintIcon.addEventListener('mouseover', this.onInputHintMouseOver.bind(this, inputHintText));
        inputHintIcon.addEventListener('mouseout', this.onInputHintMouseOut.bind(this, input, inputHintText));
      }
      // Initialize input
      input.addEventListener('focus', this.onInputFocus.bind(this, input, inputError, inputHintText));
      input.addEventListener('blur', this.onInputBlur.bind(this, inputHintText));
    }
  };

  this.onInputFocus = function(input, inputError, inputHintText) {
    if (inputError) {
      inputError.classList.add('display-none');
      // Restore placeholder text that was removed to show the error text instead
      input.placeholder = input.placeholderCopy;
    }
    if (inputHintText) {
      inputHintText.classList.remove('display-none');
    }
  };

  this.onInputBlur = function(inputHintText) {
    if (inputHintText) {
      inputHintText.classList.add('display-none');
    }
  };

  this.onInputErrorClick = function(input) {
    input.focus();
  };

  this.onInputHintMouseOver = function(inputHintText) {
    inputHintText.classList.remove('display-none');
  };

  this.onInputHintMouseOut = function(input, inputHintText) {
    // Hide hint if input isn't focused
    if (input !== document.activeElement) {
      inputHintText.classList.add('display-none');
    }
  };

  this.init();
};
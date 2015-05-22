/**
 * A simple, reusable form validator that
 *   - shows/hides input hints on hint icon hover/input focusing
 *   - hides inline error messages when clicked
 *   - can validate input fields
 *
 * @param root DOM element where validator starts looking for input fields; defaults to document
 */
var FormValidator = function(root) {
  this.init = function() {
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
        input.valueCopy = input.value;
        input.value = '';
        inputError.addEventListener('click', this.onInputErrorClick.bind(this, input, inputError));
      }
      // Initialize input's help icon
      var inputHintIcon = root.getElementById(input.id + 'HelpIcon');
      var inputHintText = root.getElementById(input.id + 'HelpText');
      if (inputHintIcon && inputHintText) {
        inputHintIcon.addEventListener('mouseover', this.onInputHintMouseOver.bind(this, inputHintText));
        inputHintIcon.addEventListener('mouseout', this.onInputHintMouseOut.bind(this, input, inputHintText));
      }
      // Initialize input
      input.addEventListener('focus', this.onInputFocus.bind(this, input, inputHintText));
      input.addEventListener('blur', this.onInputBlur.bind(this, inputHintText));
    }
  };

  this.addInputValidation = function(inputId, validator) {
    var onChange = function (e) {
      var input = e.target;
      var inputValidationError = root.getElementById(input.id + 'ValidationError');
      if (validator.isValid(input.value)) {
        inputValidationError.classList.add('display-none');
        input.classList.add('valid');
      } else {
        if(input.value.length>0){
          inputValidationError.classList.remove('display-none');
        }
        input.classList.remove('valid');

      }
    };
    var input = document.getElementById(inputId);
    input.addEventListener('change', onChange);

  };

  this.onInputFocus = function(input, inputHintText) {
    var inputError = root.getElementById(input.id + 'ErrorText');
    if (inputError) {
      inputError.remove();
      // Restore placeholder and value that was removed to show the error text instead
      input.placeholder = input.placeholderCopy;
      input.value = input.valueCopy;
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

var NameValidator = function() {
  this.isValid = function(name) {
    return name.trim().length > 0;
  }
};

var EmailValidator = function() {
  this.isValid = function(email) {
    // http://stackoverflow.com/a/46181/543875
    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    return re.test(email);
  }
};

var PasswordMatchValidator = function(password1Id) {
  this.isValid = function(password2) {
    var password1 = document.getElementById(password1Id).value;
    var passwordStrengthCalculator = new PasswordStrengthCalculator(password1Id, 'passwordStrength');
    return passwordStrengthCalculator.isValid(password1) && (password1 === password2);
  }
};
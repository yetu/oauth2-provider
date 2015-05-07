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

  this.addInputValidation = function(inputId, validator) {
    var onChange = function (input) {
      if (validator.isValid(input.value)) {
        input.classList.add('valid');
      } else {
        input.classList.remove('valid');
      }
    };
    var input = document.getElementById(inputId);
    input.addEventListener('change', onChange.bind(input));
    onChange(input);
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

var PasswordValidator = function() {
  this.isValid = function(password) {
    // Password must contain at least one lower case, one upper case, one number and have at least 8 characters
    var re = /^.*(?=.{8,})(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).*$/i;
    return re.test(password);
  }
};

var PasswordMatchValidator = function(password1Id) {
  this.isValid = function(password2) {
    var password1 = document.getElementById(password1Id).value;
    return new PasswordValidator().isValid(password1) && (password1 === password2);
  }
};
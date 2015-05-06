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
        inputError.addEventListener('click', this.onInputErrorClick.bind(this, input, inputError));
      }
      // Initialize input
      input.addEventListener('click', this.onInputClick.bind(this, input, inputError));
      input.addEventListener('focus', this.onInputClick.bind(this, input, inputError));
    }
  };

  this.onInputClick = function(input, inputError) {
    if (inputError) {
      inputError.classList.add('display-none');
      // Restore placeholder text that was removed to show the error text instead
      input.placeholder = input.placeholderCopy;
    }
  };

  this.onInputErrorClick = function(input, inputError) {
    input.focus();
  };

  this.init();
};
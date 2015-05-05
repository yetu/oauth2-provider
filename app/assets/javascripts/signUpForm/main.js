(function(){
  var inputValidators = function(inputId) {
    var nameValidator = function(name) {
      return name.trim().length > 0;
    };
    var emailValidator = function(email) {
      // http://stackoverflow.com/a/46181/543875
      var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
      return re.test(email);
    };
    var passwordValidator = function(password) {
      return password.length > 7;
    };
    var passwordMatchValidator = function(password2) {
      var password1 = document.getElementById("password2ID").value;
      return passwordValidator(password1) && (password1 === password2);
    };

    var validators = {};
    validators["firstNameID"] = nameValidator;
    validators["lastNameID"] = nameValidator;
    validators["email"] = emailValidator;
    validators["password1ID"] = passwordValidator;
    validators["password2ID"] = passwordMatchValidator;

    var input = document.getElementById(inputId);
    var validator = input ? validators[inputId] : null;
    return validator ? validator(input.value) : false;
  };

  var passwordController = new PasswordController();
  var formController = new FormController(inputValidators);
  formController.init();
}());
var PasswordStrengthCalculator = function(inputId, strengthLabelId) {
  var passwordInput = document.getElementById(inputId);

  var upperCase= new RegExp('[A-Z]');
  var lowerCase= new RegExp('[a-z]');
  var numbers = new RegExp('[0-9]');
  //if we want to have a stronger password later:
  //var specialchars = new RegExp('([!,%,&,@,#,$,^,*,?,_,~])');

  var getPasswordStrength = function(value){
    if (value.length > 8) { characters = 1; } else { characters = 0; };
    if (value.match(upperCase)) { capitalletters = 1} else { capitalletters = 0; };
    if (value.match(lowerCase)) { loweletters = 1}  else { loweletters = 0; };
    if (value.match(numbers)) { number = 1}  else { number = 0; };

    return characters + capitalletters + loweletters + number;
  };

  passwordInput.onkeyup = function(){
    var passwordStrength = getPasswordStrength(passwordInput.value);
    document.getElementById(strengthLabelId).setAttribute("class", "strength-" + passwordStrength);
  };
};
/**
 * Created by elisahilprecht on 23/04/15.
 */
var PasswordController = function(){
  var passwordFeedbackIsShown = true;
  var passwordInput = document.getElementById('password1ID');
  var helpIconPassword = document.getElementById('password1IDHelpIcon');

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

    var total = characters + capitalletters + loweletters + number;
    return total;
  };

  var updateFeedbackForUser = function(passwordStrength){
    console.log(passwordStrength);
  };

  passwordInput.onkeyup = function(){
    var password = passwordInput.value;
    var passwordStrength = getPasswordStrength(password);
    if(passwordStrength===4 && password===""){
      passwordFeedbackIsShown = false;
    }
    else{
      passwordFeedbackIsShown = true;
    }
    updateFeedbackForUser(passwordStrength)
  };
  return {
    passwordFeedbackIsShown: passwordFeedbackIsShown,
  }
};

(function(){
  var passwordController = new PasswordController();
  var formMessageController = new FormMessageController(passwordController.passwordFeedbackIsShown)
}());
/**
 * Created by elisahilprecht on 02/05/15.
 */
(function(){
  var passwordController = new PasswordController();
  var formController = new FormController(passwordController.passwordFeedbackIsShown)
  formController.init();
}());
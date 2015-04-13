/**
 * Created by elisahilprecht on 13/04/15.
 */
(function(){
  document.getElementById('submitButton').onclick = function(e){
    var emailInputField = document.getElementById('email');
    emailInputField.value =  emailInputField.value.replace(/ /g,'');
  };
})();
/**
 * Created by elisahilprecht on 13/04/15.
 */
(function(){
  var errorElement = document.getElementById('password1IDErrorText');
  if(errorElement!=undefined){
    errorElement.innerHTML = errorElement.innerHTML.split(',')[0];
  }
})();

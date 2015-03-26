var errorNames = [];
var validator = new FormValidator('registration_form', [{
  name: 'firstname',
  display: 'required',
  rules: 'required|alpha'
},
  {
    name: 'lastname',
    display: 'required',
    rules: 'required|alpha'
  },
  {
    name: 'password',
    rules: 'required|callback_check_password'
  },
  {
    name: 'repeatpassword',
    display: 'repeated password',
    rules: 'required|matches[password]'
  },
  {
    name: 'email',
    rules: 'required|valid_email'
  },
  {
    name: 'terms',
    display: 'terms &amp; conditions',
    rules: 'required'
  }
], function(errors, event) {
  for(var j=0; j<errorNames.length; j++){
    document.getElementById(errorNames[j]+'_error').setAttribute('style','display:none;');
  }
  errorNames = [];
  if (errors.length > 0) {
    //event.preventDefault();
    for(var i=0; i<errors.length; i++){
      var error = errors[i];
      error.element.setAttribute('style','border: solid 1px red;')
      if(error.rule==='required'&&error.name!='terms'){
        error.element.setAttribute('placeholder', 'This field is required.');
      }
      else{
        errorNames.push(error.name);
        document.getElementById(error.name+'_error').setAttribute('style','display:block;');
      }
    }
  };
});
validator.registerCallback('check_password', function(value) {
  //At least one upper case english letter
  //At least one lower case english letter
  //At least one digit
  //At least one special character
  //Minimum 8 in length
  var pattern =  new RegExp('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}');
  if (pattern.test(value)) {
    return true;
  }
  return false;
})
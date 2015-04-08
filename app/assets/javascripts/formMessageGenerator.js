window.FormMessageGenerator = {};
(function(FormMessageGenerator){
  FormMessageGenerator.load = function() {
    var helpInlines = document.getElementsByClassName("help-inline");
    window.setTimeout(function () {
      helpInlines = document.getElementsByClassName("help-inline");
      for (var k = 0; k < helpInlines.length; k++) {
        helpInlines[k].onclick = function () {
          var next = this
          next.setAttribute('class', 'help-inline display-none');
          var inputField = next.previousSibling.previousSibling;
          inputField.focus();
        }
      }
      ;
    }, 1000);

    var inputFields = document.getElementsByClassName("left__input");
    var helpBlocks = document.getElementsByClassName("help-block");
    for (var i = 0, l = inputFields.length; i < l; i++) {
      if (helpBlocks[i]) {
        helpBlocks[i].setAttribute('class', 'help-block display-none');
        helpBlocks[i] = null;
      }
      if (inputFields[i].value !== "") {
        helpInlines[i].setAttribute('class', 'help-inline help-inline__bottom')
      }
      if (helpInlines[i].innerHTML && helpInlines[i].innerHTML.indexOf("do not match") > -1) {
        helpInlines[i].setAttribute('class', 'help-inline help-inline__bottom')
      }
      inputFields[i].onblur = function () {
        var next;
        if (this.nextSibling && this.nextSibling.nextSibling) {
          next = this.nextSibling.nextSibling;
        }
        if (this.value != "") {
          this.setAttribute('class', 'left__input black-color');
          if (next) {
            next.setAttribute('class', 'help-inline display-none');
          }
        }
        else {
          this.setAttribute('class', 'left__input grey-color');
          if (next) {
            next.setAttribute('class', 'help-inline display-none');
          }
        }
      }
      inputFields[i].onfocus = function () {
        if (this.nextSibling && this.nextSibling.nextSibling) {
          var next = this.nextSibling.nextSibling;
          next.setAttribute('class', 'help-inline display-none');
        }
      }
    }
  }
})(window.FormMessageGenerator);
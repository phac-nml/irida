$(document).ready(function() {
  var $setPassword = $("#setpassword");
  if ($setPassword.prop('disabled')) {
    // if the activation e-mail checkbox is disabled, then we're not
    // configured to send activation e-mails to users. Uncheck the checkbox
    // and show the password entry fields.
    $setPassword.prop('checked', false);
    $('.password').attr('required', 'required').show(100);
  } else {
    // if the activation e-mail checkbox is not disabled, then we're
    // *probably* configured to send e-mails (but maybe not correctly)
    // so add a change listener to show and hide the password entry
    // fields based on whether or not the checkbox is checked.
    $setPassword.change(function() {
      if ($(this).prop('checked')) {
        $(".password").hide(100);
        $(".password").removeAttr("required");
      }
      else {
        $(".password").show(100);
        $(".password").attr("required", "required");
      }
    });

    if ($setPassword.prop('checked')) {
      $(".password").hide();
    }
  }

    //Patterns for restrictions
    var ucase = new RegExp("^.*[A-Z].*$");
    var lcase = new RegExp("^.*[a-z].*$");
    var num = new RegExp("^.*[0-9].*$");

    $("input[type=password]").keyup(function() {

        var visible = 0;
        var MIN_LENGTH = 6;
        var NUM_RULES = 4;

        var passwordField = $("#password")

        // Password must be 6 characters or longer
        if(passwordField.val().length >= MIN_LENGTH) {
            $('#password-minlength').hide(100);
            visible++;
        }else{
            $('#password-minlength').show(100);
            visible--;
        }

        // Password must contain an uppercase letter
        if(ucase.test(passwordField.val())) {
            $('#password-uppercase').hide(100);
            visible++;
        }else{
            $('#password-uppercase').show(100);
            visible--;
        }

        // Password must contain a lowercase letter
        if(lcase.test(passwordField.val())) {
            $('#password-lowercase').hide(100);
            visible++;
        }else{
            $('#password-lowercase').show(100);
            visible--;
        }

        //Password must contain a number
        if(num.test(passwordField.val())) {
            $('#password-number').hide(100);
            visible++;
        }else{
            $('#password-number').show(100);
            visible--;
        }

        // Once all are met, hide the alert
        if (visible === NUM_RULES) {
            $('#passwordRequirements').hide(100);
        }else{
            $('#passwordRequirements').show(100);
        }
    });
});

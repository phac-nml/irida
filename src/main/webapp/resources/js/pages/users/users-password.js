$(document).ready(function() {

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
$(document).ready(function() {
    $("input[type=password]").keyup(function() {
        var ucase = new RegExp("^.*[A-Z].*$");
        var lcase = new RegExp("^.*[a-z].*$");
        var num = new RegExp("^.*[0-9].*$");

        var visible = 0;
        var MIN_LENGTH = 6;
        var NUM_RULES = 4;

        // Password must be 6 characters or longer
        if($("#password").val().length >= MIN_LENGTH) {
            $('#password-minlength').hide();
            visible++;
        }else{
            $('#password-minlength').show();
            visible--;
        }

        // Password must contain an uppercase letter
        if(ucase.test($("#password").val())) {
            $('#password-uppercase').hide();
            visible++;
        }else{
            $('#password-uppercase').show();
            visible--;
        }

        // Password must contain a lowercase letter
        if(lcase.test($("#password").val())) {
            $('#password-lowercase').hide();
            visible++;
        }else{
            $('#password-lowercase').show();
            visible--;
        }

        //Password must contain a number
        if(num.test($("#password").val())) {
            $('#password-number').hide();
            visible++;
        }else{
            $('#password-number').show();
            visible--;
        }

        // Once all are met, hide the alert
        if (visible === NUM_RULES) {
            $('#passwordRequirements').hide();
        }else{
            $('#passwordRequirements').show();
        }
    });
});
(function ($) {
    $(function () {
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
            $setPassword.change(function(){
                if($(this).prop('checked')){
                    $(".password").hide(100);
                    $(".password").removeAttr("required");
                }
                else{
                    $(".password").show(100);
                    $(".password").attr("required","required");
                }
            });

            if($setPassword.prop('checked')){
                $(".password").hide();
            }
        }
    });
})(window.jQuery);
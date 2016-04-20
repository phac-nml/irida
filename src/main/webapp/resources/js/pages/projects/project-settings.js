var projectSettings = (function(page, notifications) {
    $("#assemble").change(function(){
        var checkbox = $(this);
        var assemble = checkbox.is(":checked");
        
        $.ajax({
            url: page.urls.assemble,
            type: 'POST',
            data: {
                'assemble': assemble
            }, 
            statusCode : {
                200 : function(){
                    notifications.show({'msg': page.i18n.assemble});
                }
            },
            fail : function(){
                notifications.show({'msg': page.i18n.error, type:"error"});
            }
        });
    });
    
})(window.PAGE, window.notifications);
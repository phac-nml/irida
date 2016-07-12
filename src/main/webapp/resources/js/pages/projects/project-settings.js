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
                200 : function(response){
                    notifications.show({'msg': response.result});
                }
            },
            fail : function(){
                notifications.show({'msg': page.i18n.error, type:"error"});
            }
        });
    });
    
    $(".sync-setting").change(function(){
        var freq = $(this).val();
        
        updateSyncSettings({'frequency': freq});
    });
    
    $("#forceSync").on('click', function(){
        updateSyncSettings({'forceSync': "true"});
    });
    
    $("#becomeSyncUser").on('click', function(){
        updateSyncSettings({'changeUser': "true"});
    });
    
    $(document).ready(function(){
       getApiStatus(page.vars.apiId, "#api-status", "#connect-button");
    });
    
    function updateSyncSettings(params){
        $.ajax({
            url: page.urls.sync,
            type: 'POST',
            data: params, 
            statusCode : {
                200 : function(response){
                    notifications.show({'msg': response.result});
                }
            },
            error : function(){
                notifications.show({'msg': page.i18n.error, type:"error"});
            }
        });
    }
    
})(window.PAGE, window.notifications);
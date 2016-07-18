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
       var apiId = $("#connect-button").data("api-id");
       if(apiId != null){
        getApiStatus(apiId, "#api-status", "#connect-button", showConnectedActions);
       }
    });
    
    function showConnectedActions(){
        $(".api-connected-action").show();
    }
    
    function updateSyncSettings(params){
        $.ajax({
            url: page.urls.sync,
            type: 'POST',
            data: params, 
            statusCode : {
                200 : function(response){
                    if(response.result){
                        notifications.show({'msg': response.result});
                    }
                    else if(response.error){
                        notifications.show({'msg': response.error, type:"error"});
                    }
                }
            },
            error : function(){
                notifications.show({'msg': page.i18n.error, type:"error"});
            }
        });
    }
    
})(window.PAGE, window.notifications);
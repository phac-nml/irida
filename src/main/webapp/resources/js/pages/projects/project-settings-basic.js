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
                    notifications.show({text: response.result});
                }
            },
            fail : function(){
                notifications.show({text: page.i18n.error, type:"error"});
            }
        });
    });

    $("#sistr").change(function(){
        var checkbox = $(this);
        var sistr = checkbox.is(":checked");
        
        $.ajax({
            url: page.urls.sistr,
            type: 'POST',
            data: {
                'sistr': sistr
            }, 
            statusCode : {
                200 : function(response){
                    notifications.show({text: response.result});
                }
            },
            fail : function(){
                notifications.show({text: page.i18n.error, type:"error"});
            }
        });
    });

    $("#coverage-save").on("click", function() {
        var genomeSize = $("#genome-size").val();
        var minimumCoverage = $("#minimum-coverage").val();
        var maximumCoverage = $("#maximum-coverage").val();

        $.ajax({
            url: page.urls.coverage,
            type: 'POST',
            data: {
                genomeSize: genomeSize,
                minimumCoverage: minimumCoverage,
                maximumCoverage: maximumCoverage
            }, 
            statusCode : {
                200 : function(response){
                    notifications.show({text: response.result});
                    
                    
                    if(minimumCoverage) {
                        $("#minimum-coverage-display").html(minimumCoverage + "x");
                    } else {
                        $("#minimum-coverage-display").html(page.i18n.not_set);
                    }

                    if(maximumCoverage) {
                        $("#maximum-coverage-display").html(maximumCoverage + "x");
                    } else {
                        $("#maximum-coverage-display").html(page.i18n.not_set);
                    }

                    if(genomeSize) {
                        $("#genome-size-display").html(genomeSize + "bp");
                    } else {
                        $("#genome-size-display").html(page.i18n.not_set);
                    }

                    $(".edit-coverage").toggle();
                }
            },
            fail : function(){
                notifications.show({text: page.i18n.error, type:"error"});
            }
        });
    });

    $("#edit-coverage-btn, #coverage-cancel").on("click", function() {
        $(".edit-coverage").toggle();
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

    $("#confirm-deletion").on('change', function() {
        toggleDeleteButton();
    });
    
    $(document).ready(function(){
       var apiId = $("#connect-button").data("api-id");
       if(apiId != null){
        getApiStatus(apiId, "#api-status", "#connect-button", showConnectedActions);
       }

       toggleDeleteButton();
    });
    
    function showConnectedActions(){
        $(".api-connected-action").show();
    }

    function toggleDeleteButton(){
        if($("#confirm-deletion").is(":checked")) {
            $("#submit-delete").prop('disabled', false);
        }
        else {
            $("#submit-delete").prop('disabled', true);
        }
    }
    
    function updateSyncSettings(params){
        $.ajax({
            url: page.urls.sync,
            type: 'POST',
            data: params, 
            statusCode : {
                200 : function(response){
                    if(response.result){
                        notifications.show({text: response.result});
                    }
                    else if(response.error){
                        notifications.show({text: response.error, type:"error"});
                    }
                }
            },
            error : function(){
                notifications.show({text: page.i18n.error, type:"error"});
            }
        });
    }
    
})(window.PAGE, window.notifications);

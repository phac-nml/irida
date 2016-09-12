$(document).ready(function(){
    var connectButtonId = "#connect-button";
    $("#api-selection").on("change", function(){
        $(connectButtonId).addClass("hidden");
        
        var apiId = $(this).val();
        $(connectButtonId).data("api-id", apiId);
        getApiStatus(apiId ,"#api-status", connectButtonId);
        
        getApiProjects(apiId);    
    });
    
    $("#project-select").on("change", function(){
        var projectUrl = $(this).val();
        
        if(projectUrl == 0){
            projectUrl = null;
        }
        
        $("#projectUrl").val(projectUrl);
    });
    
    var originalApiId = $("#api-selection").val();
    
    $(connectButtonId).data("api-id", originalApiId);
    getApiStatus(originalApiId, "#api-status", connectButtonId);
    getApiProjects(originalApiId);
});

function getApiProjects(apiId){
    var url = /*[[@{/projects/ajax/api/}]]*/ "/projects/ajax/api/";
    url = url + apiId;
    
    $(".project-option").remove();
    $("#projectUrl").val("");
    
    var projectSelect = $("#project-select");
    
    $.ajax({
        url: url,
        success: function(vals){
            $(".project-option").remove();
            
            $.each(vals, function (i, response) {
                var project = response.project;
                var status = response.remoteStatus;
                var projectUrl = status.url;
                projectSelect.append('<option class="project-option" value="'+projectUrl+'">'+project.label+'</option>');
            });
            projectSelect.prop("disabled", false);
        },
        error: function(){
            projectSelect.prop("disabled", true);
        }
    });
}
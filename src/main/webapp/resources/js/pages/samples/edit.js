$(document).ready(function(){
    $("#edit-form").submit(function(){
        var metadata = {};
        $("#other-metadata").find(".metadata-entry").each(function(){
            var entry = $(this);
            var key = entry.find(".metadata-key").val();
            var value = entry.find(".metadata-value").val();
            metadata[key] = value;
        });

        $("#metadata").val(JSON.stringify(metadata));
    });
});
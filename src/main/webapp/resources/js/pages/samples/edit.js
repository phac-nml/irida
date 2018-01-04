$(document).ready(function() {
  /**
   * Serialize metadata to json for submission
   */
  $("#edit-form").submit(function() {
    var metadata = {};
    $("#other-metadata")
      .find(".metadata-entry")
      .each(function() {
        var entry = $(this);
        var key = entry.find(".metadata-key").val();
        var value = entry.find(".metadata-value").val();
        metadata[key] = { value: value, type: "text" };
      });

    // paste the json into a hidden text field for submission
    if (Object.keys(metadata).length > 0) {
      $("#metadata").val(JSON.stringify(metadata));
    }
  });

  /**
   * Remove a metadata term
   */
  $(".delete-metadata").on("click", function() {
    $(this)
      .closest(".metadata-entry")
      .remove();
  });

  /**
   * Add a metadata term from the template
   */
  $("#add-metadata").on("click", function() {
    var newMetadata = $("#metadata-template").clone(true);
    newMetadata.removeAttr("id");
    $("#metadata-fields").append(newMetadata);
  });
});

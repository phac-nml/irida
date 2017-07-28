$(document).ready(function() {
  checkTypes();

  //after any file is selected, ensure the types are the same
  $(".concat").on("click", function() {
    checkTypes();
  });

  //on submit disable the submit button and show a message
  $("#concatenate-form").submit(function() {
    $("#form-submit").prop("disabled", true);
    $("#submit-info").show();
  });
});

/**
 * Check if the selected filetypes match
 */
function checkTypes() {
  var type = "none";
  var count = 0;
  var different = false;

  // for each checked file check if they match
  $(".concat:checked").each(function() {
    count++;
    var checkType = $(this).data("object-type");
    if (type === "none") {
      type = checkType;
    } else if (type != checkType) {
      different = true;
    }
  });

  // ensure the files are the same type and at least 2 are selected
  if (count >= 2 && !different) {
    $("#form-submit").prop("disabled", false);
    $("#type-warning").hide();
  } else {
    $("#form-submit").prop("disabled", true);
    $("#type-warning").show();
  }

  return different;
}

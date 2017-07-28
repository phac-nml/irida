$(document).ready(function() {
  checkTypes();

  $(".concat").on("click", function() {
    checkTypes();
  });

  $("#concatenate-form").submit(function() {
    $("#form-submit").prop("disabled", true);
    $("#submit-info").show();
  });
});

function checkTypes() {
  var type = "none";
  var count = 0;
  var different = false;
  $(".concat:checked").each(function() {
    count++;
    var checkType = $(this).data("object-type");
    if (type === "none") {
      type = checkType;
    } else if (type != checkType) {
      different = true;
    }
  });

  if (count >= 2 && !different) {
    $("#form-submit").prop("disabled", false);
    $("#type-warning").hide();
  } else {
    $("#form-submit").prop("disabled", true);
    $("#type-warning").show();
  }

  return different;
}

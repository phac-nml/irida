$("#name").keyup(function() {
  if ($("#name").val().length > 3) {
    $("#createGroupButton").removeAttr("disabled");
  } else {
    $("#createGroupButton").attr("disabled", "disabled");
  }
});

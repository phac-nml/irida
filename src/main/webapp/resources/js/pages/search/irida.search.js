// Handles admin search link to send admin search parameter
$("#search-admin-link").on("click", function() {
  $("#admin-global-search").attr("checked", true);
  $("#global-search").submit();
});

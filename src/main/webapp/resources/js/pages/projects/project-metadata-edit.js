import "jquery";
import "../../vendor/plugins/jquery/select2";

$(".organism-select").select2({
  placeholder: window.PAGE.i18n.organism,
  minimumInputLength: 2,
  ajax: {
    // instead of writing the function to execute the request we use Select2's convenient helper
    url: window.PAGE.urls.taxonomy,
    dataType: "json",
    data: function(term, page) {
      return {
        searchTerm: term // search term
      };
    },
    results: function(data, page) {
      // parse the results into the format expected by Select2.
      // since we are using custom formatting functions we do not need to alter remote JSON data
      return { results: data };
    }
  }
});

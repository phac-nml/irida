import $ from "jquery";
import "../../../../vendor/plugins/jquery/select2";

/*
Set up the projects Select2 input
 */
const $input = $("#js-projectsSelect");
const url = $input.data("url");
console.log("FOOBAR");
$input.select2({
  theme: "bootstrap",
  minimumInputLength: 3,
  ajax: {
    url,
    data(params) {
      return {
        term: params.term || "foobar",
        page: params.page || 1
      };
    }
  }
});

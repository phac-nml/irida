import angular from "angular";
import "../../vendor/plugins/jquery/select2";

$("#new-organism-warning").hide();
$(".organism-select")
  .select2({
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
    },
    initSelection: function(element, callback) {
      var organism = $(element).val();
      if (organism !== "") {
        $.ajax(window.PAGE.urls.taxonomy, {
          data: { searchTerm: organism },
          dataType: "json"
        }).done(function(data) {
          callback(data[0]);
        });
      }
    }
  })
  .on("change", function(data) {
    if (data.added.searchTerm) {
      $("#new-organism-warning").show();
    } else {
      $("#new-organism-warning").hide();
    }
  })
  .select2("val", window.PAGE.project.organism);

$("#useCartSamples").change(function() {
  showHideCart(100);
});

showHideCart(0);

function showHideCart(time) {
  if ($("#useCartSamples").prop("checked")) {
    $("#cartAlert").show(time);
  } else {
    $("#cartAlert").hide(time);
  }
}

/*
    Disables/enables the lockSamples checkbox
    depending on if using the samples from cart
*/
$("#useCartSamples").on("change", function(e) {
  var selected = $(e.target).prop("checked");
  if (!selected) {
    $("#lockSamples").attr("disabled", true);
    $("#lockSamples").prop("checked", false);
  } else {
    $("#lockSamples").removeAttr("disabled");
  }
});

const newProjectModule = angular
  .module("irida.project.new", ["ui.bootstrap"])
  .controller("NewProjectController", function() {
    var vm = this;
    vm.project = {
      name: window.PAGE.project.name,
      remoteURL: window.PAGE.project.remoteURL
    };
  }).name;
angular.module("irida").requires.push(newProjectModule);

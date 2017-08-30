import $ from "jquery";
import "./../../vendor/plugins/jquery/select2";
import "jquery-validation";

const form = $("#create-sample-form");
const saveBtn = $("#save-btn");

$.validator.addMethod("checkallowedchars", function(value) {
  return /^[A-Z\d_-]+$/i.test(value);
});

form.validate({
  errorElement: "em",
  errorPlacement: function(error, element) {
    error.addClass("help-block");
    error.insertAfter(element);
  },
  highlight(element) {
    $(element)
      .parents(".form-group")
      .addClass("has-error")
      .removeClass("has-success");
  },
  unhighlight(element) {
    $(element)
      .parents(".form-group")
      .addClass("has-success")
      .removeClass("has-error");
  },
  rules: {
    sampleName: {
      required: true,
      minlength: 3,
      checkallowedchars: true
    }
  },
  submitHandler: function(form) {
    saveBtn.attr("disabled", true);
    form.submit();
  }
});

form.find("input").on("keyup blur", () => {
  saveBtn.prop("disabled", form.valid() ? false : "disabled");
});

// import angular from "angular";
// import "angular-messages";
// import "./../../vendor/plugins/jquery/select2";
//
// const URL_BASE = `${window.TL.BASE_URL}projects/${window.PAGE.project
//   .id}/samples`;
//
// /**
//    * Service to communicate with the server API.
//    * @param $http
//    * @returns {{createSample: createSample}}
//    * @constructor
//    */
// function SampleService($http) {
//   return {
//     createSample: createSample
//   };
//
//   /**
//      * Create a new sample
//      * @param sample
//      * @param successFn Success Callback
//      * @param errorFn Error Callback
//      * @returns {*}
//      */
//   function createSample(sample, successFn, errorFn) {
//     return $http
//       .post(URL_BASE, sample)
//       .then(function(response) {
//         successFn(response.data);
//       })
//       .catch(function(response) {
//         errorFn(response.data);
//       });
//   }
// }
//
// /**
//    * Custom validation directive for the name input field
//    *   Only letter, numbers, underscores, and dashes allowed.
//    * @returns {{restrict: string, require: string, link: Function}}
//    */
// function nameValidator() {
//   const re = /[^A-Za-z0-9\-_!@#\$%~`]/;
//   return {
//     restrict: "A",
//     require: "ngModel",
//     link: function(scope, elem, attrs, ctrl) {
//       ctrl.$validators.nameValidator = function(value) {
//         return !re.test(value);
//       };
//     }
//   };
// }
//
// /**
//    * Allows for the clearing of the server message once the user has changed the input on the field.
//    * @returns {{restrict: string, require: string, link: Function}}
//    */
// function serverValidated() {
//   return {
//     restrict: "A",
//     require: "ngModel",
//     link: function(scope, elem, attrs, ctrl) {
//       ctrl.$validators.server = function() {
//         return true;
//       };
//     }
//   };
// }
//
// /**
//    * Custom Select2 directive for searching through the organism ontology using
//    * JQuery Select2 plugin.
//    * @returns {{restrict: string, require: string, link: Function}}
//    */
// function select2($timeout) {
//   return {
//     restrict: "A",
//     require: "ngModel",
//     link: function(scope, elem, attrs, ctrl) {
//       $(elem)
//         .select2({
//           minimumInputLength: 3,
//           ajax: {
//             url: `${window.TL.BASE_URL}projects/ajax/taxonomy/search`,
//             dataType: "json",
//             data: function(term) {
//               return {
//                 searchTerm: term
//               };
//             },
//             results: function(data) {
//               return {
//                 results: data
//               };
//             }
//           }
//         })
//         .on("change", function(data) {
//           scope.$apply(function() {
//             if (ctrl.$validators.custom !== data.added.searchTerm) {
//               $timeout(function() {
//                 ctrl.$validators.custom = data.added.searchTerm;
//               }, false);
//             }
//           });
//         });
//     }
//   };
// }
//
// /**
//    * Main page controller.
//    * @param sampleService
//    * @constructor
//    */
// function SampleController(sampleService) {
//   const vm = this;
//   vm.sample = {};
//   vm.uploader = {
//     inProgress: false
//   };
//   vm.nameOptions = {
//     debounce: 300
//   };
//
//   vm.createSample = function createSample() {
//     vm.sample.sequencerSampleId = vm.sample.sampleName;
//     sampleService.createSample(
//       vm.sample,
//       sampleCreatedSuccess,
//       sampleCreatedError
//     );
//   };
//
//   function sampleCreatedSuccess(response) {
//     vm.sample = response.sample;
//     window.location = `${URL_BASE}/${response.sampleId}/sequenceFiles`;
//   }
//
//   function sampleCreatedError(response) {
//     var errors = response.errors;
//     for (var key in errors) {
//       if (
//         errors.hasOwnProperty(key) &&
//         key !== "label" &&
//         key !== "sequencerSampleId"
//       ) {
//         vm.sampleDetailForm[key].$dirty = true;
//         vm.sampleDetailForm[key].$setValidity("server", false);
//         vm.sampleDetailForm[key].serverError = errors[key];
//       }
//     }
//   }
// }
//
// const app = angular.module("irida");
//
// app.requires.push("ngMessages");
//
// app
//   .factory("SampleService", ["$http", SampleService])
//   .directive("select2", ["$timeout", select2])
//   .directive("serverValidated", [serverValidated])
//   .directive("nameValidator", [nameValidator])
//   .controller("SampleController", [
//     "SampleService",
//     "$uibModal",
//     SampleController
//   ]);

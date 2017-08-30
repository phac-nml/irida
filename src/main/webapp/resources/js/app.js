import $ from "jquery";
import angular from "angular";
import _ from "lodash";

const deps = _.union(window.dependencies || [], [
  "ngAria",
  "ngAnimate",
  "ui.bootstrap",
  "irida.session",
  "irida.notifications",
  "irida.cart"
]);

const app = angular.module("irida", deps).config($httpProvider => {
  $httpProvider.defaults.headers.post["Content-Type"] =
    "application/x-www-form-urlencoded";

  // Make sure that all ajax form data is sent in the correct format.
  $httpProvider.defaults.transformRequest = data => {
    if (typeof data === "undefined") {
      return data;
    }
    return $.param(data);
  };
});

export default app;

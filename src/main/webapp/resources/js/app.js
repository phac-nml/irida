import $ from "jquery";
import angular from "angular";
import _ from "lodash";
import "./modules/cart/irida.cart";
import { IridaSession } from "./services/IridaSession";
import "./pages/search/irida.search";
/*
This will load notifications into the global namespace.  Remove this once all
files have been converted over to wekbpack builds.
 */
import "./modules/notifications";

const deps = _.union(window.dependencies || [], [
  "ngAria",
  "ngAnimate",
  "ui.bootstrap",
  "irida.cart",
  IridaSession
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

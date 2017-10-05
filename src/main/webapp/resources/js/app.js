import $ from "jquery";
import angular from "angular";
import _ from "lodash";
import "./modules/cart/irida.cart";
import { IridaSession } from "./services/IridaSession";
import "./pages/search/irida.search";

const deps = _.union(window.dependencies || [], [
  "ngAria",
  "ngAnimate",
  "ui.bootstrap",
  "irida.notifications",
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

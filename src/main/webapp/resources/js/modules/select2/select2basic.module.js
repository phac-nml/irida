import $ from "jquery";
import angular from "angular";
import "../../vendor/plugins/jquery/select2.js";

/*
 * Turns a pre-populated select input into a select2.
 * Usage:
 *  <select select2-basic="">
 *    <options>...</options>
 *  </select>
 */
const Select2Basic = () => {
  return {
    restrict: "A",
    link($scope, $elm) {
      const field = $elm[0];
      $(field).select2();
    }
  };
};

export const Select2BasicModule = angular
  .module("select2.basic", [])
  .directive("select2Basic", Select2Basic).name;

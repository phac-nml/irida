import angular from "angular";
import carousel from "angular-ui-bootstrap/src/carousel";

angular.module("irida", [carousel]).controller("ImageController", function() {
  this.slides = window.PAGE.images.map((i, index) => ({ ...i, index }));
});

import angular from "angular";
import { CART } from "../../utilities/events-utilities";
import { removeSample } from "../../apis/cart/cart";

function CartService(scope, $http) {
  const svc = this;
  const urls = {
    project: window.TL.BASE_URL + "cart/project/"
  };

  svc.clear = function() {
    //fire a DELETE to the server on the cart then broadcast the cart update event
    return $http.delete(urls.all).then(function() {
      const event = new Event(CART.UPDATED);
      document.dispatchEvent(event);
    });
  };

  svc.removeProject = function(projectId) {
    return $http.delete(urls.project + projectId).then(function() {
      const event = new Event(CART.UPDATED);
      document.dispatchEvent(event);
    });
  };

  svc.removeSample = function(projectId, sampleId) {
    return removeSample(projectId, sampleId).then(detail => updateCart(detail));
  };
}

const updateCart = detail => {
  const event = new CustomEvent(CART.UPDATED, { detail });
  document.dispatchEvent(event);
};

angular
  .module("irida.cart", [])
  .service("CartService", ["$rootScope", "$http", CartService]);

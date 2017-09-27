/**
 * This file will eventually replace irida.cart.js
 * The goal is to simplify interaction with the cart by removing angularjs
 * from the flow.
 */
import $ from "jquery";
import { CART } from "../../utilities/events-utilities";

/**
 * Add samples to the global cart.
 * @param {object} projects {projectId: [sampleIds]}
 */
export function addSamplesToCart(projects) {
  const promises = [];
  /*
  For each project that has samples, post the project/samples to
  and store the promise.
   */
  Object.keys(projects).forEach(projectId => {
    promises.push(
      $.post(window.TL.URLS.cart.add, {
        projectId,
        sampleIds: projects[projectId]
      }).then(response => {
        /*
        Display a notification of what occurred on the server.
         */
        window.notifications.show({
          msg: response.message
        });
      })
    );
  });

  /*
  Wait until all the projects have been added to the server cart, and
  then notify the UI that this has occurred.
   */
  $.when.apply($, promises).done(function() {
    const event = new Event(CART.UPDATED);
    document.dispatchEvent(event);
  });
}

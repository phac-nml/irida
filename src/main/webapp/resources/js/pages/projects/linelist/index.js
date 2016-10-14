const angular = require('angular');
import linelistService from './factories/linelist.service';
import templateService from './factories/template.service';
import linelist from './components/linelist.component';

const app = angular.module('irida');

app
  .service('linelistService', linelistService)
  .service('templateService', templateService)
  .component('linelist', linelist);

const templateSelect = document.querySelector('#template-select');
templateSelect.addEventListener('change', function(event) {
  broadcast('LINELIST_TEMPLATE_CHANGE', {template: event.target.value});
});

/**
 * Use AngularJS broadcast system to send a message to all listeners.
 * @param {string} name of event
 * @param {args} args to send to the listener
 */
function broadcast(name, args) {
  var elm = document.querySelector('[ng-app]');
  var scope = angular.element(elm).scope();
  scope.$broadcast(name, args);
}

const angular = require('angular');
import {LineListModule} from './components/linelist/linelist.module';

const app = angular.module('irida');
app.requires.push(LineListModule);

/**
 * Listening for changes to the template selection select input.
 */
const templateSelect = document.querySelector('#template-select');
if (templateSelect) {
  templateSelect.addEventListener('change', function(event) {
    broadcast('LINELIST_TEMPLATE_CHANGE', {template: event.target.value});
  });
}

/**
 * Use AngularJS broadcast system to send a message to all listeners.
 * @param {string} name of event
 * @param {args} args to send to the listener
 */
function broadcast(name, args) {
  const elm = document.querySelector('[ng-app]');
  const scope = angular.element(elm).scope();
  scope.$broadcast(name, args);
}

/**
 * IRIDA Notification Module
 *
 * This script allows notification to be called through jQuery or angular using the same API:
 *
 * notificatoins.show({
 *      'msg' : 'the main body',
 *      'type': 'success' // Optional from: 'success', 'error', 'information', 'warning'
 *      'timeout': 5000 // Optional false will make it go away only on click.  Or pass number of milliseconds.
 *    });
 */

(function (angular) {
  "use strict";
  window.notifications = (function() {
    var types = {'success':true, 'error':true, 'information':true, 'warning': true},
        animation = {
      open  : 'animated fadeInRight', // Animate.css class names
      close: 'animated fadeOutRight' // Animate.css class names
    };

    /**
     * Show notification
     * @param o {Object} see above.
     * @returns the Noty object.
     */
    function show(o) {
      if(o.msg === undefined) {
        throw new Error("No 'msg' attribute pass to the the notification");
      }
      var options   = {
        "theme"    : 'relax',
        "layout" : 'topRight',
        "type"   : o.type !== undefined && types[o.type] ? o.type : 'success',
        "text" : o.msg,
        "animation": animation
      };

      var n = noty(options);

      /**
       * Noty does not close on its own.  Set a default timeout to 5 seconds.
       * Can be overridden.
       */
      if(o.timeout !== false) {
        window.setTimeout(function() {
          n.close();
        }, o.timeout === undefined ? 5000 : o.timeout);
      }
      return n;
    }

    return {
      show: show
    };
  })();

  angular.module('irida.notifications', [])
    .service('notifications', [function () {
      var svc = this;
      svc.show = function (o) {
        window.notifications.show(o);
      };
    }]);
})(window.angular);
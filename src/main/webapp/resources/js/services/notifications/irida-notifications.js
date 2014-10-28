/* jshint undef: true, unused: true */
/* global angular, _ */

angular.module('irida.notifications', ['toaster'])
    .service('notifications', ['toaster', function (toaster) {
        "use strict";
        var svc = this,
            opts = {
                type: 'success',
                title: null,
                msg: null
            };
        svc.show = function (o) {
            o = _.extend(_.clone(opts), o);
            toaster.pop(o.type, o.title, o.msg);
        };
    }]);
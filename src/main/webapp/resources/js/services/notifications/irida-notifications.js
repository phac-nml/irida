/* jshint undef: true, unused: true */
/* global angular, _ */

angular.module('irida.notifications', ['toastr'])
    .service('notifications', ['toastr', function (toastr) {
        "use strict";
        var svc = this,
            types = ['success', 'info', 'error', 'warning'],
            opts = {
                type: 'success',
                title: null,
                msg: null,
                template: null,
                time: 3000,
                posn: 'toast-top-right'
            };
        svc.show = function (o, options) {
            options = options || {};
            o = _.extend(_.clone(opts), o);
            if(_.contains(types, o.type)) {
                toastr[o.type](o.msg, o.title, options);
            }
        };
    }]);
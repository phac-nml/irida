/* global angular, NGS */
/**
 * Created by josh on 2013-07-05.
 */
(function (ng, app) {
    'use strict';
    app.factory('projectService', [function () {
        return {
            project: {},
            user: {}
        }
    }]);
})(angular, NGS);

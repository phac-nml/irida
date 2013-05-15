/**
 * User: josh
 * Date: 2013-05-15
 * Time: 10:33 AM
 */

var CONFIG;

(function () {

  var templateUrlPrefix = '/partials/';

  CONFIG = {

    routing: {
      html5Mode: true
    },


    templateFileSuffix: '.html',

    prepareViewTemplateUrl: function (url) {
      return this.viewUrlPrefix + url + this.templateFileSuffix;
    }
  };

})();

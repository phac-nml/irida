/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2013-05-15
 * Time: 8:51 AM
 * To change this template use File | Settings | File Templates.
 */

angular.module('NGS.Services', ['http-auth-interceptor-buffer'])
  .factory('loginService', ['Base64', '$cookieStore', '$http', function (Base64, $cookieStore, $http) {
    'use strict';
    $http.defaults.headers.common['Authorization'] = 'Basic ' + $cookieStore.get('authdata');
    return {
      setHeader: function (username, password, callback) {
        if (typeof username === 'string' && typeof password === 'string') {
          var encoded = Base64.encode(username + ':' + password);
          $http.defaults.headers.common.Authorization = 'Basic ' + encoded;
          $cookieStore.put('authdata', encoded);
          if (typeof callback === 'function') {
            callback();
          }
        }
      },
      deleteHeader: function () {
        $cookieStore.remove('authdata');
        $http.defaults.headers.common.Authorization = 'Basic ';
      }
    };
  }])
  .factory('Base64', function () {
    var keyStr = 'ABCDEFGHIJKLMNOP' +
      'QRSTUVWXYZabcdef' +
      'ghijklmnopqrstuv' +
      'wxyz0123456789+/' +
      '=';
    return {
      encode: function (input) {
        var output = "";
        var chr1, chr2, chr3 = "";
        var enc1, enc2, enc3, enc4 = "";
        var i = 0;

        do {
          chr1 = input.charCodeAt(i++);
          chr2 = input.charCodeAt(i++);
          chr3 = input.charCodeAt(i++);

          enc1 = chr1 >> 2;
          enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
          enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
          enc4 = chr3 & 63;

          if (isNaN(chr2)) {
            enc3 = enc4 = 64;
          } else if (isNaN(chr3)) {
            enc4 = 64;
          }

          output = output +
            keyStr.charAt(enc1) +
            keyStr.charAt(enc2) +
            keyStr.charAt(enc3) +
            keyStr.charAt(enc4);
          chr1 = chr2 = chr3 = "";
          enc1 = enc2 = enc3 = enc4 = "";
        } while (i < input.length);

        return output;
      },

      decode: function (input) {
        var output = "";
        var chr1, chr2, chr3 = "";
        var enc1, enc2, enc3, enc4 = "";
        var i = 0;

        // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
        var base64test = /[^A-Za-z0-9\+\/\=]/g;
        if (base64test.exec(input)) {
          alert("There were invalid base64 characters in the input text.\n" +
            "Valid base64 characters are A-Z, a-z, 0-9, '+', '/',and '='\n" +
            "Expect errors in decoding.");
        }
        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

        do {
          enc1 = keyStr.indexOf(input.charAt(i++));
          enc2 = keyStr.indexOf(input.charAt(i++));
          enc3 = keyStr.indexOf(input.charAt(i++));
          enc4 = keyStr.indexOf(input.charAt(i++));

          chr1 = (enc1 << 2) | (enc2 >> 4);
          chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
          chr3 = ((enc3 & 3) << 6) | enc4;

          output = output + String.fromCharCode(chr1);

          if (enc3 != 64) {
            output = output + String.fromCharCode(chr2);
          }
          if (enc4 != 64) {
            output = output + String.fromCharCode(chr3);
          }

          chr1 = chr2 = chr3 = "";
          enc1 = enc2 = enc3 = enc4 = "";

        } while (i < input.length);

        return output;
      }
    };
  })
  .factory('ajaxService', ['$http', '$q', function ($http, $q) {
    'use strict';
    return {
      /**
       * create - use to create a new resource on the server
       * @param url
       * @param data
       * @returns callback promise for success and failure.
       */
      create: function (url, data) {
        var deferred = $q.defer();
        $http({
          method: 'POST',
          url: url,
          data: data,
          headers: {
            'Content-Type': 'application/json'
          }
        })
          .success(function (data, status, headers, config) {
            deferred.resolve(headers('location'));
          })
          .error(function (data) {
            // TODO: (JOSH - 2013-05-10) Handle create errors
            deferred.reject(data);
          });
        return deferred.promise;
      },
      /**
       * get- use to get a resource from the server
       * @param url
       * @returns {*}
       */
      get: function (url) {
        if (url) {
          var deferred = $q.defer();
          $http({
            url: url,
            method: 'GET'
          })
            .success(function (data) {
              deferred.resolve(data);
            })
            .error(function () {
              // TODO: (JOSH - 2013-05-10) Handle get errors properly
              deferred.reject('An error occurred during get @ ' + url);
            });

          return deferred.promise;
        }
        return false;
      },
      /**
       * post
       * @param url
       * @param data
       * @returns {*}
       */
      post: function (url, data) {
        if (url) {
          var deferred = $q.defer();
          $http({
            url: url,
            method: 'POST',
            params: data
          })
            .success(function (data) {
              deferred.resolve(data);
            })
            .error(function () {
              // TODO: (JOSH - 2013-05-10) Handle post errors properly
              deferred.reject('An error occurred while posting @ ' + url);
            });

          return deferred.promise;
        }
        return false;
      },
      /**
       * patch - uses to update a resource (usually one field)
       * @param url
       * @param data
       * @returns {*}
       */
      patch: function (url, data) {
        if (url && data) {
          var deferred = $q.defer();

          $http({
            method: 'PATCH',
            url: url,
            data: data,
            headers: {
              'Content-Type': 'application/json'
            }
          })
            .success(function (data) {
              deferred.resolve(data);
            })
            .error(function (data) {
              deferred.reject(data);
            });
          return deferred.promise;
        }
        return false;
      },
      /**
       * delete - delete resource from server
       * @param url
       * @returns {*}
       */
      deleteItem: function (url) {
        if (url) {
          var deferred = $q.defer();

          $http({
            method: 'DELETE',
            url: url,
            headers: {
              'Content-Type': 'application/json'
            }
          })
            .success(function (data) {
              deferred.resolve(data);
            })
            .error(function (data, status, headers, config) {
              console.log(data);
              console.log(status);
              console.log(headers);
              console.log(config);
              deferred.reject(status);
            });
          return deferred.promise;
        }
        return false;
      }
    };
  }])
  .factory('authService', ['$rootScope', 'httpBuffer', function ($rootScope, httpBuffer) {
    return {
      /**
       * call this function to indicate that authentication was successfull and trigger a
       * retry of all deferred requests.
       * @param data an optional argument to pass on to $broadcast which may be useful for
       * example if you need to pass through details of the user that was logged in
       */
      loginConfirmed: function (data) {
        $rootScope.$broadcast('event:auth-loginConfirmed', data);
        httpBuffer.retryAll();
      }
    };
  }])
  .factory('resourceService', [function () {
    'use strict';
    var formatLinks = function (links) {
      var l = {};
      angular.forEach(links, function (val) {
        l[val.rel] = val.href;
      });
      return l;
    };

    return {
      formatResourceLinks: function (links) {
        return formatLinks(links);
      },
      formatRelatedResource: function (relatedResource) {
        // - links
        //   \- to resource
        //   \- to delete resource
        // - label
        // - dateCreated
        var resource = [];
        angular.forEach(relatedResource, function (r) {
          var links = formatLinks(r.links);
          resource.push({label: r.label, dateCreated: r.dateCreated, links: links});
        });
        return resource;
      }
    };
  }])

/**
 * $http interceptor.
 * On 401 response (without 'ignoreAuthModule' option) stores the request
 * and broadcasts 'event:angular-auth-loginRequired'.
 */
  .config(['$httpProvider', function ($httpProvider) {

    var interceptor = ['$rootScope', '$q', 'httpBuffer', function ($rootScope, $q, httpBuffer) {
      'use strict';
      function success(response) {
        return response;
      }

      function error(response) {
        if (response.status === 401 && !response.config.ignoreAuthModule) {
          var deferred = $q.defer();
          httpBuffer.append(response.config, deferred);
          $rootScope.$broadcast('event:auth-loginRequired');
          return deferred.promise;
        }
        // otherwise, default behaviour
        return $q.reject(response);
      }

      return function (promise) {
        return promise.then(success, error);
      };

    }];
    $httpProvider.responseInterceptors.push(interceptor);
  }]);

/**
 * Private module, an utility, required internally by 'http-auth-interceptor'.
 */
angular.module('http-auth-interceptor-buffer', [])

  .factory('httpBuffer', ['$injector', '$cookieStore', function ($injector, $cookieStore) {
    /** Holds all the requests, so they can be re-requested in future. */
    var buffer = [];

    /** Service initialized later because of circular dependency problem. */
    var $http;

    function retryHttpRequest(config, deferred) {
      function successCallback(response) {
        deferred.resolve(response);
      }

      function errorCallback(response) {
        deferred.reject(response);
      }

      $http = $http || $injector.get('$http');
      $http(config).then(successCallback, errorCallback);
    }

    return {
      /**
       * Appends HTTP request configuration object with deferred response attached to buffer.
       */
      append: function (config, deferred) {
        buffer.push({
          config: config,
          deferred: deferred
        });
      },

      /**
       * Retries all the buffered requests clears the buffer.
       */
      retryAll: function () {
        for (var i = 0; i < buffer.length; ++i) {
          buffer[i].config.headers.Authorization = 'Basic ' + $cookieStore.get('authdata');
          retryHttpRequest(buffer[i].config, buffer[i].deferred);
        }
        buffer = [];
      }
    };
  }]);
/* global angular */

angular.module('irida', ['irida.services']);

angular.module('irida.services', ['ngResource'])
	.service('AjaxService', function($http, $q) {
	'use strict';
	return {
		create: function(url, data) {
			var deferred = $q.defer();
			$http({
				method: 'POST',
				url: url,
				data: data,
				headers: {
					'Content-Type': 'application/json'
				}
			})
				.success(function(data) {
				deferred.resolve(data);
			})
				.error(function(data) {
				deferred.reject(data);
			});
			return deferred.promise;
		},
		get: function(url) {
			if (url) {
				var deferred = $q.defer();

				$http.get(url)
					.success(function(data) {
					deferred.resolve(data);
				})
					.error(function() {
					deferred.reject('An error occurred while getting projects');
				});

				return deferred.promise;
			}
		},
		patch: function(url, data) {
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
					.success(function(data) {
					deferred.resolve(data);
				})
					.error(function(data) {
					deferred.reject(data);
				});
				return deferred.promise;
			}
		},
    delete: function(url) {
      if (url) {
        var deferred = $q.defer();

        $http({
          method: 'DELETE',
          url: url,
          headers: {
            'Content-Type': 'application/json'
          }
        })
          .success(function(data) {
            deferred.resolve(data);
          })
          .error(function(data) {
            deferred.reject(data);
          });
        return deferred.promise;
      }
    }
	};
});
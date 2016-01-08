(function(ng, lodash, page){
	"use strict";

	function samplesService ($http, $q) {
		var samples;

		// Private Methods

		function getProjectSamples() {
			return $http.get(page.urls.project);
		}

		// Public Methods

		function fetchSamples(options) {
			var promises = [],
			    // By default only load project samples
			    config = { project: true, local: false, remote: false };

			lodash.merge(config, options);

			if(config.project) {
				promises.push(getProjectSamples());
			}

			return $q.all(promises).then(function (responses) {
				samples = samples || [];
				responses.forEach(function (response) {
					if (response.data.hasOwnProperty("samples")) {
						samples = samples.concat(response.data.samples);
					}
				});
				return samples;
			});
		}

		return {
			fetchSamples: fetchSamples
		};
	}

	ng.module("irida.projects.samples.service", [])
		.factory("samplesService", ["$http", "$q", samplesService]);
	;
})(window.angular, window._, window.PAGE);
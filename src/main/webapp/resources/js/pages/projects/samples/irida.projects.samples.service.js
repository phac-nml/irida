(function(ng, $, _, page){
	"use strict";

	/**
   * Service to handle server calls for project samples.
   * @param {Object} $http Angular http object.
   * @param {Object} $q Angular promises.
   * @param compiledNotification
   * @param cartService
   * @returns {{fetchSamples: fetchSamples}}.
	 */
	function SamplesService ($http, $q, compiledNotification, cartService) {
    /**
     * Current projects to display.  If this does not change, then there is no reason to complete an ajax request.
     */
    var _options = {},
        /**
         * List of the current samples based on the _options.
         */
        _samples;
		
		// Private Methods

    /**
     * Get the samples that are specific for the current project.
     * @returns {*}
     */
		function getProjectSamples() {
			return $http.get(page.urls.samples.project);
		}

    /**
     * Get the samples that are for a local project.
     * @param id - identifier for the local project.
     * @returns {*} Promise
     */
		function getLocalSamples(id) {
			var url = page.urls.samples.local.replace(/{id}/, id);
			return $http.get(url);
		}

    function getSamples(options) {
      var promises = [],
          // By default only load project samples
          config   = {project: true, local: [], remote: [], showNotification: true};

      _.merge(config, options);

      // Add the project samples (if required).
      if(config.project) {
        promises.push(getProjectSamples());
      }
      if (config.local.length > 0) {
        config.local.forEach(function (id) {
          promises.push(getLocalSamples(id));
        });
      }

      return $q.all(promises).then(function (responses) {
        var samples = [], items = [];
        responses.forEach(function (response) {
          if (response.data.hasOwnProperty("samples")) {
            samples = samples.concat(response.data.samples);
            // This is adding information to build up the message to display to the users so that
            // they know how many samples from which project were added to the table.
            items.push({samples: response.data.samples.length, project: response.data.project.label});
          }
        });
        if (config.showNotificaiton) {
          // Show a notification of the currently displayed samples only if it is not
          // on the page load (ie. current project samples only)
          compiledNotification.show(items, "samplesUpdate.html", {type: "information"});
        }
        _samples = samples;
        return samples;
      });
    }


    // Public Methods

		/**
		 * Get the appropriate samples from the server.
		 * @param {Object} options - which samples to get from the server.
		 * @returns {Promise.<T>|*}
		 */
		function fetchSamples(options) {
      if (ng.equals(_options, options)) {
        return $q(function (resolve) {
          resolve(_samples);
        });
      } else {
        _options = options;
        return getSamples(options);
      }
		}

		function addSamplesToCart(samples) {
			cartService.add(samples.map(function(sample) {
				return ({
					type: sample.sampleType,
					project: sample.project.identifier,
					sample: sample.sample.identifier
				});
			}));
		}

		function removeSamples(items) {
			var sampleIds = [];
			items.forEach(function (item) {
				sampleIds.push(item.sample.identifier);
			});
			if (sampleIds.length > 0) {
				return $http.post(page.urls.samples.remove, {sampleIds: sampleIds})
					.success(function (data) {
						if (data.result === 'success') {
							notifications.show({type: data.result, msg: data.message});
						}
					});
			}
		}

		function mergeSamples(data) {
			var params = {
				sampleIds: data.ids,
				mergeSampleId: data.mergeSampleId,
				newName: data.newName
			};
			return $http.post(page.urls.samples.merge, params)
				.success(function(result) {
          notifications.show({type: result.result, msg: result.message});
				});
		}

		function copySamples(params) {
			return $http.post(page.urls.samples.copy, params)
				.success(function (result) {
					showCopyRemoveErrors(result);
				});
		}

		function moveSamples(params) {
			params.remove = true;
			return $http.post(page.urls.samples.copy, params)
				.success(function (result) {
					showCopyRemoveErrors(result)
				});
		}

		function showCopyRemoveErrors(result) {
			if (result.message) {
				notifications.show({type: "success", msg: result.message});
			}
			if (result.warnings) {
				result.warnings.forEach(function(warning) {
					notifications.show({type: "warning", msg: warning});
				})
			}
		}

		return {
			copySamples     : copySamples,
			moveSamples     : moveSamples,
			fetchSamples    : fetchSamples,
			addSamplesToCart: addSamplesToCart,
			removeSamples   : removeSamples,
			mergeSamples    : mergeSamples
		};
	}

	function AssociatedProjectsService($http) {
		function getLocalAssociated() {
			return $http.get(page.urls.associated.local);
		}

		return {
			getLocal: getLocalAssociated
		};
	}

	/**
	 * Service for creating the angular datatable
	 * @param $compile Angular compile - enables loading directives into the table
	 * @param $templateCache - gets scripts that are angular templates.
	 * @param DTOptionsBuilder - Angular datatables table options object
	 * @param DTColumnDefBuilder - Angular datatables column definition builder
	 * @returns {{createTableOptions: createTableOptions, createTableColumnDefs: createTableColumnDefs, initTable:
	 *   initTable}}
	 */
	function TableService($compile, $templateCache, DTOptionsBuilder, DTColumnDefBuilder) {
		var table;
		function createTableOptions() {
			return DTOptionsBuilder.newOptions()
				// Which row to sort by: Modified date, Descending.
				.withOption("aaSorting", [2, "desc"])
				// Add extra DOM features. See [https://datatables.net/reference/option/dom]
				// This matches the other tables in the project see datatables.properties to see full layout.
				.withDOM("<'row filter-row'<'col-sm-7 buttons'><'col-sm-5'<'filter-area'>f>><'row' <'col-md-6 col-sm-12 counts'> <'col-md-6 col-sm-12 datatables-active-filters'1>><'panel panel-default''<'row'<'col-sm-12'tr>>><'row'<'col-sm-3'l><'col-sm-6'p><'col-sm-3 text-right'i>>");
		}

		// Create any special columns in the table.
		function createTableColumnDefs() {
			return [
				// The checkbox column should not be sortable
				DTColumnDefBuilder.newColumnDef(0).notSortable()
			];
		}

		/**
		 * Initials DOM surrounding the datatable.
		 * @param $scope - angular $scope surrounding the table.
		 * @param instance - the datatable instance.
		 */
		function initTable($scope, instance) {
			table = instance;
			// Once the datatable is created add the accessory components.
			table.DataTable.on("draw.dt", function () {
				ng.element(".buttons").html($compile($templateCache.get("buttons.html"))($scope));
				ng.element(".counts").html($compile($templateCache.get("selectedCounts.html"))($scope));
				ng.element(".filter-area").replaceWith($compile($templateCache.get("filterButtons.html"))($scope))
			}).on("page.dt, order.dt, search.dt", function () {
				// Let the controller now when data changes in the table
				$scope.$emit("DATATABLE_UPDATED");
			});
		}

		return {
			createTableOptions   : createTableOptions,
			createTableColumnDefs: createTableColumnDefs,
			initTable            : initTable
		};
	}

	ng.module("irida.projects.samples.service", ["datatables", "irida.notifications", "irida.cart"])
		.service("SamplesService", ["$http", "$q", "compiledNotification", "CartService", SamplesService])
		.service("AssociatedProjectsService", ["$http", "$q", AssociatedProjectsService])
		.service("TableService", ["$compile", "$templateCache", "DTOptionsBuilder", "DTColumnDefBuilder", TableService]);
})(window.angular, window.jQuery, window._, window.PAGE);

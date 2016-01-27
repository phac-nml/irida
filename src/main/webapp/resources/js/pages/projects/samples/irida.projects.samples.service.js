(function(ng, $, lodash, page){
	"use strict";

	/**
	 * Service to handle server calls for project samples.
	 * @param {Object} $http Angular http object.
	 * @param {Object} $q Angular promises.
	 * @returns {{fetchSamples: fetchSamples}}.
	 */
	function samplesService ($http, $q, compiledNotification) {
		var initialized = false; // Show samples loading notification only if not the first load
		// Private Methods
		function getProjectSamples() {
			return $http.get(page.urls.samples.project);
		}

		function getLocalSamples(id) {
			var url = page.urls.samples.local.replace(/{id}/, id);
			return $http.get(url);
		}

		// Public Methods

		/**
		 * Get the appropriate samples from the server.
		 * @param {Object} options - which samples to get from the server.
		 * @returns {Promise.<T>|*}
		 */
		function fetchSamples(options) {
			var promises = [],
			    // By default only load project samples
			    config   = {project: true, local: [], remote: []};

			lodash.merge(config, options);

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
						items.push({samples: response.data.samples.length, project: response.data.project.label});
					}
				});
				if (initialized) {
					compiledNotification.show(items, "samplesUpdate.html", {type: "information"});
				}
				else {
					initialized = true;
				}
				return samples;
			});
		}

		return {
			fetchSamples: fetchSamples
		};
	}

	function associatedProjectsService($http, $q) {
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
	 * @returns {{createTableOptions: createTableOptions, createTableColumnDefs: createTableColumnDefs, selectAllNone:
	 *   selectAllNone, initTable: initTable}}
	 */
	function tableService($compile, $templateCache, DTOptionsBuilder, DTColumnDefBuilder) {
		var table;
		function createTableOptions() {
			return DTOptionsBuilder.newOptions()
				// Which row to sort by: Modified date, Descending.
				.withOption("order", [[2, "desc"]])
				// Add extra DOM features. See [https://datatables.net/reference/option/dom]
				// This matches the other tables in the project see datatables.properties to see full layout.
				.withDOM("<'row filter-row'<'col-sm-9 buttons'><'col-sm-3'0f>><'row' <'col-md-6 col-sm-12 counts'> <'col-md-6 col-sm-12 datatables-active-filters'1>><'panel panel-default''<'row'<'col-sm-12'tr>>><'row'<'col-sm-3'l><'col-sm-6'p><'col-sm-3 text-right'i>>");
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

	ng.module("irida.projects.samples.service", ["datatables", "irida.notifications"])
		.factory("samplesService", ["$http", "$q", "compiledNotification", samplesService])
		.factory("associatedProjectsService", ["$http", "$q", associatedProjectsService])
		.factory("tableService", ["$compile", "$templateCache", "DTOptionsBuilder", "DTColumnDefBuilder", tableService]);
})(window.angular, window.jQuery, window._, window.PAGE);
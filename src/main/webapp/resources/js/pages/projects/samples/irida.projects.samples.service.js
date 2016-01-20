(function(ng, $, lodash, page){
	"use strict";

	/**
	 * Service to handle server calls for project samples.
	 * @param {Object} $http Angular http object.
	 * @param {Object} $q Angular promises.
	 * @returns {{fetchSamples: fetchSamples}}.
	 */
	function samplesService ($http, $q) {
		var samples;

		// Private Methods

		function getProjectSamples() {
			return $http.get(page.urls.project);
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
			    config = { project: true, local: false, remote: false };

			lodash.merge(config, options);

			// Add the project samples (if required).
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
			});
		}

		function getPageSampleIds() {
			console.log(table);
			return table.dataTable._('tr', {"filter":"applied"});
		}

		return {
			createTableOptions   : createTableOptions,
			createTableColumnDefs: createTableColumnDefs,
			initTable            : initTable,
			getPageSampleIds     : getPageSampleIds
		};
	}

	ng.module("irida.projects.samples.service", ["datatables"])
		.factory("samplesService", ["$http", "$q", samplesService])
		.factory("tableService", ["$compile", "$templateCache", "DTOptionsBuilder", "DTColumnDefBuilder", tableService]);
})(window.angular, window.jQuery, window._, window.PAGE);
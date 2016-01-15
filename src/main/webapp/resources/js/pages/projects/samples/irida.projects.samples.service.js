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

	function tableService($compile, $templateCache, DTOptionsBuilder, DTColumnDefBuilder) {
		var table;
		function createTableOptions() {
			return DTOptionsBuilder.newOptions()
				.withOption("order", [[2, "desc"]])
				.withSelect({
					style:    'multi'
				})
				.withDOM("<'row filter-row'<'col-sm-9 buttons'><'col-sm-3'0f>><'row datatables-active-filters'1><'panel panel-default''<'row'<'col-sm-12'tr>>><'row'<'col-sm-3'l><'col-sm-6'p><'col-sm-3 text-right'i>>");
		}

		function createTableColumnDefs() {
			return [
				DTColumnDefBuilder.newColumnDef(0).notSortable()
			];
		}

		function initTable($scope, instance) {
			table = instance;
			table.DataTable.on("draw.dt", function () {
				ng.element(".buttons").html($compile($templateCache.get("buttons.html"))($scope));
			});
		}

		function selectAllNone(selectAll) {
			if (selectAll) {
				table.DataTable.rows({filter: 'applied'}).select();
			} else {
				table.DataTable.rows({filter: 'applied'}).deselect();
			}
		}

		return {
			createTableOptions   : createTableOptions,
			createTableColumnDefs: createTableColumnDefs,
			selectAllNone        : selectAllNone,
			initTable            : initTable
		};
	}

	ng.module("irida.projects.samples.service", ["datatables", "datatables.select"])
		.factory("samplesService", ["$http", "$q", samplesService])
		.factory("tableService", ["$compile", "$templateCache", "DTOptionsBuilder", "DTColumnDefBuilder", tableService]);
	;
})(window.angular, window._, window.PAGE);
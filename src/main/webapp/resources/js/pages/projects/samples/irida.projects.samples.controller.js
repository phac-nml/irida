(function (ng) {
	"use strict";

	/**
	 * Controller for the Project Samples Page.
	 * @param {Object} $scope Angular scope for this controller.
	 * @param {Object} $log Angular logging service.
	 * @param {Object} samplesService Service to handle server calls for samples.
	 * @param {Object} tableService Service to handle rendering the datatable.
	 * @constructor
	 */
	function SamplesController($scope, $log, samplesService, tableService) {
		var vm = this, index = 0;

		// BUTTON STATE
		vm.disabled = {
			lessThanTwo: true,
			lessThanOne: true
		};

		// Create the datatable.
		vm.dtColumnDefs = tableService.createTableColumnDefs();
		vm.dtOptions = tableService.createTableOptions();

		// Get the samples - automatically added to datatable.
		samplesService.fetchSamples().then(function (samples) {
			vm.samples = samples;
		});

		vm.selectAll = function () {
			// Select via datatables
			tableService.selectAllNone(vm.allSelected);

			// Update the samples, this updates the checkboxes.
			vm.samples.forEach(function (sample) {
				sample.selected = vm.allSelected;
			});
			updateButtons();
		};

		// Datatables selects the row, need to update the angular models.
		vm.rowClick = function($event, $index, item) {
			console.log("CLICKED");
			if($event.shiftKey) {

			}
			else  {
				$event.stopPropagation();
				item.selected = !item.selected;
				tableService.selectRow($event.currentTarget, item.selected);
			}
			updateButtons();
		};

		vm.checkboxClick = function($event, $index, item) {
			$event.stopPropagation();
			index = $index;
			var rows = $("tbody tr");
			rows.forEach(function (row) {
				console.log($(row).data("index"));
			});
			console.log();
			updateButtons();
		};

		vm.displayProjectsModal = function() {
			$log.warn("TODO: Implement displaying multiple projects");
		};

		vm.merge = function () {
			$log.warn("TODO: Implement merge functionality");
		};

		vm.copy = function () {
			$log.warn("TODO: Implement copy functionality");
		};

		vm.move = function () {
			$log.warn("TODO: Implement move functionality");
		};

		vm.delete = function () {
			$log.warn("TODO: Implement delete functionality");
		};

		vm.addToCart = function () {
				$log.warn("TODO: Implement add to cart functionality");
			};

		// This properly adds the buttons to the table.
		vm.dtInstanceCallback = function(instance) {
			tableService.initTable($scope, instance);
		};

		/**
		 * Determine how many samples are selected and update the buttons.
		 */
		function updateButtons(){
			var count = vm.samples.filter(function (sample) {
				return sample.selected;
			}).length;
			vm.disabled = {
				lessThanTwo: count < 2,
				lessThanOne: count < 1
			};
		}
	}

	ng.module("irida.projects.samples.controller", ["irida.projects.samples.service"])
		.controller("SamplesController", ["$scope", "$log",  "samplesService", "tableService", SamplesController]);
})(window.angular);
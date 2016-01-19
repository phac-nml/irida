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
		var vm = this;
		vm.selected = [];

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
		 * Responsible for selecting all or none of the samples
		 */
		vm.selectAll = function() {
			vm.selected = [];
			vm.samples.forEach(function (sample) {
				sample.selected = vm.allSelected;
				vm.selected.push(sample);
			});
			vm.updateButtons();
		};

		/**
		 * Determine how many samples are selected and update the buttons.
		 */
		vm.updateButtons = function (){
			var count = vm.samples.filter(function (sample) {
				return sample.selected;
			}).length;
			vm.allSelected = vm.samples.length === count;
			vm.disabled = {
				lessThanTwo: count < 2,
				lessThanOne: count < 1
			};
		}
	}

	ng.module("irida.projects.samples.controller", ["irida.projects.samples.service", "selectionModel"])
		.controller("SamplesController", ["$scope", "$log",  "samplesService", "tableService", SamplesController]);
})(window.angular);
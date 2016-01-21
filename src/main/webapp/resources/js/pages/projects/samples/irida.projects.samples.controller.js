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
		var vm = this, previousIndex = null;
		vm.selected = [];

		$scope.$on("DATATABLE_UPDATED", function () {
			previousIndex = null;
		});

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
		vm.selectAll = function($event) {
			$event.stopPropagation();
			vm.selected = [];
			vm.samples.forEach(function (sample) {
				sample.selected = vm.allSelected;
				if(vm.allSelected) vm.selected.push(sample);
			});
			updateButtons();
		};

		/**
		 * Handles user clicking the datatable row.  Updates selected samples
		 * @param $event
		 * @param item
		 */
		vm.rowClick = function($event, $index) {
			$event.stopPropagation();
			var item = vm.samples[$index];

			// Start by selecting or deselecting the item
			if(item.selected) {
				vm.selected.push(item);
			}
			else {
				vm.selected.splice(vm.selected.indexOf(item), 1);
			}

			// Check for multiple selection
			if (!item.selected) {
				// This would be a deselection, and would result in no further actions.
				previousIndex = null;
			} else if (previousIndex !== null && $event.shiftKey) {
				// Multi-select here
				// Get the table rows
				var found = false;
				ng.element('tbody tr').each(function (i, row) {
					var rowIndex = ng.element(row).data("index"),
						rowItem = vm.samples[rowIndex];

					if(rowIndex === $index || rowItem === previousIndex) { found = !found; }
					if(found && !rowItem.selected) {
						rowItem.selected = true;
						vm.selected.push(rowItem);
					}
				});
				updateButtons();
			} else {
				previousIndex = item;
			}

			updateButtons();
		};

		/**
		 * Selection of all samples on the current page.
		 */
		vm.selectPage = function () {
			var rows = ng.element("tbody tr");
			rows.each(function(index, row) {
				var index = $(row).data("index"),
					item = vm.samples[index];
				if(!item.selected) {
					item.selected = true;
					vm.selected.push(item);
				}
			})
		};

		/**
		 * Determine how many samples are selected and update the buttons.
		 */
		function updateButtons(){
			var count = vm.selected.length;
			vm.allSelected = vm.samples.length === count;
			vm.disabled = {
				lessThanTwo: count < 2,
				lessThanOne: count < 1
			};
		}
	}

	ng.module("irida.projects.samples.controller", ["irida.projects.samples.service"])
		.controller("SamplesController", ["$scope", "$log",  "samplesService", "tableService", SamplesController]);
})(window.angular);
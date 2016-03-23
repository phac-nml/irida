(function (ng, $, page) {
	"use strict";

	/**
	 * Controller for the Project Samples Page.
	 * @param {Object} $scope Angular scope for this controller.
	 * @param {Object} $log Angular logging service.
	 * @param {Object} samplesService Service to handle server calls for samples.
	 * @param {Object} tableService Service to handle rendering the datatable.
	 * @constructor
	 */
	function SamplesController($scope, $filter, modalService, samplesService, tableService) {
		var vm = this, previousIndex = null,
		    // Which projects to display
		    display = {
			    project: true,
			    local: []
		    };
		vm.samples = [];
		vm.selected = [];

		$scope.$on("DATATABLE_UPDATED", function () {
			previousIndex = null;
		});

		$scope.$watch("samplesCtrl.samples", function () {
			updateButtons();
		}, true);

		// BUTTON STATE
		vm.disabled = {
			lessThanTwo: true,
			lessThanOne: true,
			otherProjects: false
		};

		// Hide project name unless multiple displayed.
		vm.showProjectname = false;

		// Create the datatable.
		vm.dtColumnDefs = tableService.createTableColumnDefs();
		vm.dtOptions = tableService.createTableOptions();

		function displaySamples() {
			samplesService.fetchSamples(display).then(function (samples) {
				// Need to know if the sample should be selected, and remove any that are no longer in the table.;
				var s = [];
				samples.forEach(function (sample) {
					if (vm.selected.find(function (item) {
							// Check to see if the selected item matches the sample and from the right project.
							if (item.sample.identifier === sample.sample.identifier &&
								item.project.identifier === sample.project.identifier) {
								s.push(item);
								return true;
							}
						})) {
						sample.selected = true;
					}
				});
				// Update the samples that are selected and currently in the table.
				vm.selected = s;
				// Determine if the project name needs to be displayed in the table.
				vm.showProjectname = display.local.length > 0;
				// Update the samples;
				vm.samples = samples;
				updateButtons();
			});
		}

		vm.displayProjectsModal = function () {
			modalService.openAssociatedProjectsModal(display)
				.then(function (projectsToDisplay) {
					// Check to make sure their are updates to the table to process.
					if (!ng.equals(projectsToDisplay, display)) {
						display = projectsToDisplay;
						displaySamples();
					}
				})
		};

		vm.merge = function () {
			var ids = [];
			vm.selected.forEach(function (item) {
				ids.push(item.sample.identifier);
			});
			modalService.openMergeModal(vm.selected).then(function (result) {
				samplesService.mergeSamples(result).then(function () {
					// Need to reload the samples since the data has changed.
					samplesService.fetchSamples({showNotification: false}).then(function (samples) {
						vm.samples = samples;
						vm.selected = [];
					});
				});
			});
		};

		vm.copy = function () {
			modalService.openCopyModal(vm.selected).then(function (result) {
				samplesService.copySamples(result).then(function () {
					// No need to reload since it was only a copy
					// Just clear the selected ones.
					vm.selected.forEach(function (i) {
						i.selected = false;
					});
					vm.selected = [];
				});
			});
		};

		vm.move = function () {
			modalService.openMoveModal(vm.selected).then(function (result) {
				samplesService.moveSamples(result).then(function () {
					// Need to reload the samples since the data has changed.
					samplesService.fetchSamples({showNotification: false}).then(function (samples) {
						vm.samples = samples;
						vm.selected = [];
					});
				});
			});
		};

		vm.delete = function () {
			modalService.openRemoveModal(vm.selected).then(function () {
				samplesService.removeSamples(vm.selected).then(function () {
					vm.samples = vm.samples.filter(function (sample) {
						return !sample.selected;
					});
					vm.selected = [];
				});
			});

		};

		vm.addToCart = function () {
			var selected = vm.samples.filter(function (sample) {
				return sample.selected;
			});
			samplesService.addSamplesToCart(selected);
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
				if(vm.allSelected) {
					vm.selected.push(sample)};
			});
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

					// Check to see if it was the previous clicked row or the currently clicked row.
					// This will mark the beginning or end of the selections.
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
			rows.each(function(i, row) {
				var index = $(row).data("index"),
					item = vm.samples[index];
				if(!item.selected) {
					item.selected = true;
					vm.selected.push(item);
				}
			});
		};

		vm.openFilter = function () {
			modalService.openFilterModal().then(function (filter) {
				vm.samples = $filter("samplesFilter")(vm.samples, filter);
			});
		};

		/**
		 * Determine how many samples are selected and update the buttons.
		 */
		function updateButtons(){
			var count = vm.selected.length;
			vm.allSelected = vm.samples.length === count;
			vm.disabled = {
				lessThanTwo: count < 2,
				lessThanOne: count < 1,
				otherProjects: display.local.length > 0
			};
		}

		// Load data into the table;
		displaySamples();
	}

	function samplesFilter () {
		return function(samples, filter) {
			return samples.filter(function (s) {
				return (filter.name === undefined || s.sample.sampleName.contains(filter.name))
			});
		};
	}

	ng.module("irida.projects.samples.controller", ["irida.projects.samples.service", "irida.projects.samples.modals", "ngMessages", "ui.bootstrap"])
		.controller("SamplesController", ["$scope", "$filter", "modalService",  "SamplesService", "TableService", SamplesController])
		.filter("samplesFilter", [samplesFilter])
	;
})(window.angular, window.jQuery, window.PAGE);

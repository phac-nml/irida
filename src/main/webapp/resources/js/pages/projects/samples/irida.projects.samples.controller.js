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
	function SamplesController($scope, $log, $uibModal, samplesService, tableService) {
		var vm = this, previousIndex = null,
		    // Which projects to display
		    display = {
			    project: true,
			    local: []
		    };
		vm.selected = [];

		$scope.$on("DATATABLE_UPDATED", function () {
			previousIndex = null;
		});



		// BUTTON STATE
		vm.disabled = {
			lessThanTwo: true,
			lessThanOne: true
		};

		// Hide project name unless multiple displayed.
		vm.showProjectname = false;

		// Create the datatable.
		vm.dtColumnDefs = tableService.createTableColumnDefs();
		vm.dtOptions = tableService.createTableOptions();

		// Get the samples - automatically added to datatable.
		samplesService.fetchSamples().then(function (samples) {
			vm.samples = samples;
		});

		vm.displayProjectsModal = function() {
			var modal = $uibModal.open({
				templateUrl: "associated-projects.modal.html",
				controllerAs: "associatedProjectsCtrl",
				controller: "AssociatedProjectsCtrl",
				resolve: {
					display: function () {
						return display;
					}
				}
			});

			modal.result.then(function (items) {
				// Check to make sure their are updates to the table to process.
				if (!ng.equals(items, display)) {
					display = items;
					samplesService.fetchSamples(items).then(function (samples) {
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
					});
				}
			})
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
			var ids = [], modal;
			vm.selected.forEach(function (item) {
				ids.push(item.sample.identifier);
			});

			modal = $uibModal.open({
				size        : 'lg',
				templateUrl : page.urls.modals.remove + "?" + $.param({sampleIds: ids}),
				openedClass : 'remove-modal',
				controllerAs: "removeCtrl",
				controller  : ["$uibModalInstance", function RemoveSamplesController($uibModalInstance) {
					var vm = this;

					vm.cancel = function () {
						$uibModalInstance.dismiss();
					};

					vm.remove = function () {
						$uibModalInstance.close();
					};
				}]
			});

			modal.result.then(function () {
				samplesService.removeSamples(vm.selected).then(function () {
					vm.samples = vm.samples.filter(function (sample) {
						return !sample.selected;
					});
					vm.selected = [];
					updateButtons();
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

	function AssociatedProjectsCtrl($uibModalInstance, associatedProjectsService, display) {
		var vm = this;
		vm.projects = {};
		vm.display = ng.copy(display);
		vm.local = {};

		// Get the local project
		associatedProjectsService.getLocal().then(function (result) {
			// Check to see if they are already displayed.
			result.data.forEach(function(project) {
				project.selected = vm.display.local.indexOf(project.identifier) > -1;
			});
			vm.projects.local = result.data;
		});

		vm.close = function () {
			$uibModalInstance.dismiss();
		};

		vm.showProjects = function () {
			// Just want the ids
			vm.display.local = [];
			vm.projects.local.forEach(function (project) {
				if (project.selected) {
					vm.display.local.push(project.identifier);
				}
			});

			$uibModalInstance.close(vm.display);
		};
	}

	ng.module("irida.projects.samples.controller", ["irida.projects.samples.service", "ui.bootstrap"])
		.controller("SamplesController", ["$scope", "$log", "$uibModal",  "samplesService", "tableService", SamplesController])
		.controller("AssociatedProjectsCtrl", ["$uibModalInstance", "associatedProjectsService", "display", AssociatedProjectsCtrl])
	;
})(window.angular, window.jQuery, window.PAGE);
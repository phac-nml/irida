(function (ng) {
	"use strict";

	function SamplesController($scope, $log, samplesService, tableService) {
		var vm = this, selected = [];
		vm.disabled = {
			lessThanTwo: true,
			lessThanOne: true
		};

		vm.dtColumnDefs = tableService.createTableColumnDefs();
		vm.dtOptions = tableService.createTableOptions();
		samplesService.fetchSamples().then(function (samples) {
			vm.samples = samples;
		});

		vm.selectAll = function () {
			var search = tableService.selectAllNone(vm.allSelected);
			// Update the samples
			vm.samples.forEach(function (sample, index) {
				sample.selected = vm.allSelected;
			});
			updateButtons();
		};

		vm.rowClick = function(item) {
			// Means it is about to become unselected
			item.selected = !item.selected;
			if(item.selected) {
				selected.push(item);
			}
			else {
				var index = selected.indexOf(item);
				if(index > -1) {
					selected.splice(index, 1);
				}
			}
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

		vm.dtInstanceCallback = function(instance) {
			tableService.initTable($scope, instance);
		};

		function updateButtons(){
			var count = _.filter(vm.samples, {selected: true}).length;
			vm.disabled = {
				lessThanTwo: count < 2,
				lessThanOne: count < 1
			};
		}
	}

	ng.module("irida.projects.samples.controller", ["irida.projects.samples.service"])
		.controller("SamplesController", ["$scope", "$log",  "samplesService", "tableService", SamplesController]);
})(window.angular);
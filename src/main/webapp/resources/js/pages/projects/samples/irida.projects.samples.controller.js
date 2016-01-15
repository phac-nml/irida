(function (ng) {
	"use strict";

	function getFilteredSamples() {
		return function(samples, term, filter) {
			return samples.map(function (sample) {

			});
		};
	}

	function SamplesController($scope, $log, samplesService, tableService) {
		var vm = this, selected = [];
		vm.disabled = {
			lessThanTwo: true,
			lessThanOne: true
		};

		vm.dtOptions = tableService.createTableOptions();
		samplesService.fetchSamples().then(function (samples) {
			vm.samples = samples;
		});

		vm.updateAllSelected = function () {
			// Find out what the current filter is.
			var search = tableService.getSearchTerm();
			console.log(search);
			vm.samples.forEach(function (sample, index) {
				vm.selected[index] = vm.allSelected;
			});
			updateEnabled();
		};

		vm.sampleChanged = function (index) {
			vm.allSelected = vm.selected.length === vm.samples.length;
			updateEnabled();
		};

		vm.rowClick = function($event, item) {
			// Means it is about to become unselected
			if(!$($event.currentTarget).hasClass("selected")) {
				selected.push(item);
			}
			else {
				var index = selected.indexOf(item);
				if(index > -1) {
					selected.splice(index, 1);
				}
			}
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

		function updateEnabled() {
			// Remove false values
			for(var prop in vm.selected) {
				if(!vm.selected[prop]){
					delete vm.selected[prop];
				}
			}
			// Update UI buttons depending on the number of samples selected
			var count = Object.keys(vm.selected).length;
			vm.disabled = {
				lessThanTwo: count < 2,
				lessThanOne: count < 1
			};
		}
	}

	ng.module("irida.projects.samples.controller", ["irida.projects.samples.service"])
		.controller("SamplesController", ["$scope", "$log",  "samplesService", "tableService", SamplesController]);
})(window.angular);
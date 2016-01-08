(function (ng) {
	"use strict";

	function SamplesController(service, DTOptionsBuilder) {
		var vm = this;
		vm.selected = {};

		vm.dtOptions = DTOptionsBuilder.newOptions()
			.withDOM('pitrfl');

		vm.updateAllSelected = function () {
			vm.samples.forEach(function (sample, index) {
				vm.selected[index] = vm.allSelected;
			});
		};

		vm.sampleChanged = function (index) {
			if (vm.selected.length === vm.samples.length) {
				vm.allSelected = true;
			} else {
				vm.allSelected = false;
			}
		};

		service.fetchSamples().then(function (samples) {
			vm.samples = samples;
		});
	}

	ng.module("irida.projects.samples.controller", ["irida.projects.samples.service", "datatables"])
		.controller("SamplesController", ["samplesService", "DTOptionsBuilder", SamplesController]);
	;
})(window.angular);
(function (ng) {
	"use strict";

	function SamplesController(service, DTOptionsBuilder) {
		var vm = this;
		vm.selected = {};

		vm.dtOptions = DTOptionsBuilder.newOptions()
			.withDOM("<'row filter-row'<'col-sm-6'2><'col-sm-6'0f>><'row datatables-active-filters'1><'panel panel-default''<'row'<'col-sm-12'tr>>><'row'<'col-sm-3'l><'col-sm-6'p><'col-sm-3 text-right'i>>");

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
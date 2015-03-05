(function () {
    "use strict";

    function CartController($scope, $timeout, cart) {
        "use strict";
        var vm = this,
          initialized = false;
        vm.show = false;
        vm.projects = [];
        vm.count = 0;
        vm.collapsed = {};

        $scope.$on('cart.update', function () {
            getCart(false);
        });

        function getCart (collapse) {
            cart.all()
              .then(function (data) {
                var prev = vm.count;
                vm.count = 0;
                  vm.projects = data;
                  _.each(data, function(p) {
                      vm.count += p.samples.length;
                      if(collapse){
                        vm.collapsed[p.id] = true;
                      }
                  });
                if (initialized && prev !== vm.count) {
                  vm.animation = 'glow';
                  $timeout(function () {
                    vm.animation = '';
                  }, 3000);
                } else {
                  // This is just to prevent animation on page load.
                  initialized = true;
                }
              });
        }


        getCart(true);
    }

  /**
   * Controller for functions on the cart slider
   * @param CartService The cart service to communicate with the server
   */
    function CartSliderController(CartService, $modal){
      "use strict";

      var vm = this;

      vm.clear = function(){
        CartService.clear();
      };

      vm.removeProject = function(projectId){
        CartService.removeProject(projectId);
      }

      vm.removeSample = function(projectId,sampleId){
        CartService.removeSample(projectId,sampleId);
      }
      
      vm.exportToGalaxy = function() {
    	var project;
    	
    	CartService.all()
    		.then(function (data) {
    			if(data != null) {
    				$modal.open({
    					templateUrl: TL.BASE_URL + 'cart/template/galaxy/project/' + data[0].id,
    					controller : 'GalaxyCartDialogCtrl as gCtrl'
    				});
    			}
    			

    		});
    		
      

      }
    }

    function CartDirective() {
        return {
            restrict    : "E",
            templateUrl : "/cart.html",
            replace: true,
            controllerAs: "cart",
            controller  : ['$scope', '$timeout', 'CartService', CartController]
        }
    }

    function CartService(scope, $http, $q) {
        var svc = this,
            urls = {
                all: TL.BASE_URL + "cart",
                add: TL.BASE_URL + "cart/add/samples",
                project: TL.BASE_URL + "cart/project/"
            };

        svc.all = function () {
            return $http.get(urls.all)
              .then(function (response) {
                  if (response.data) {
                      return response.data.projects
                  }
                  else {
                      return [];
                  }
              });
        };

        svc.add = function (samples) {
          var promises = [];

          _.forEach(samples, function(s) {
            promises.push($http.post(urls.add, {projectId: s.project, sampleIds: [s.sample]}));
          });

          $q.all(promises).then(function(){
            scope.$broadcast("cart.update", {});
          });
        };

      svc.clear = function () {
        //fire a DELETE to the server on the cart then broadcast the cart update event
        return $http.delete(urls.all).then(function () {
          scope.$broadcast("cart.update", {});
        });
      };

      svc.removeProject = function(projectId){
        $http.delete(urls.project+projectId).then(function () {
          scope.$broadcast("cart.update", {});
        })
      };

      svc.removeSample = function(projectId,sampleId){
        $http.delete(urls.project+projectId+"/samples/"+sampleId).then(function () {
          scope.$broadcast("cart.update", {});
        })
      }

    }
    
    function GalaxyExportService() {
    	"use strict";
    	var svc = this;
    	var params = [];
    	var samples = [];
    	svc.initialize = function() {
    		       samples = [];
    		       params = {
    		         "_embedded" : {
    		    	   "samples" : samples
    		    	  }
    		       };
    	};
    	svc.addSampleFile = function(sampleName,sampleFilePath) {
    		var sample = _.find(samples,function(sampleItr) {return (sampleItr.name == sampleName)});
    		if(sample == null) {
    			sample = {
        				"name" : sampleName,
        			    "_links": {"self" : {"href" : "http://sample/path/can/go/here"}},
        			    "_embedded" : {"sample_files" : []}
        			};
    			samples.push(sample);
    			}
    		sample._embedded.sample_files.push({'_links' : {'self' : {'href' : sampleFilePath}}});
    	};
    	svc.setLibrary = function(libraryName) {
    		params._embedded.library = {"name" : libraryName};
    	};
    	svc.setUserEmail = function(email) {
    		params._embedded.user = {"email" : email};
    	}
    	svc.getSampleFormEntities = function() {
    		var sampleFormEntity = {
    			"name" : "json_params",
    			"value": JSON.stringify(params)
    		};
    		return [sampleFormEntity];
    	};
    }
    
    function GalaxyCartDialogCtrl($modalInstance, $timeout, GalaxyExportService, cart) {
    	"use strict";
    	var vm = this
    	
    	vm.upload = function () {
    		vm.uploading = true;
    		
    		cart.all()
			.then(function (data) {
				var projects = data;
				GalaxyExportService.initialize();
				GalaxyExportService.setUserEmail(vm.email);
	    		GalaxyExportService.setLibrary(vm.name);
				_.each(projects,function(project){
					var samples = project.samples;
					_.each(samples,function(sample){
						var sequenceFiles = sample.sequenceFiles
						_.each(sequenceFiles,function(sequenceFile){
							GalaxyExportService.addSampleFile(sample.label, sequenceFile.href);
						})
					})
				});

	    		vm.sampleFormEntities = GalaxyExportService.getSampleFormEntities();
	    	    $timeout(function(){
	    	    	document.getElementById("galSubFrm").submit();
	    	    	vm.close();
	    	    	}); 
	    	});        
    	};
    	vm.setName = function (name) {
            vm.name = name;
            };
        vm.setEmail = function (email) {
        	vm.email = email;
        	};
        vm.close = function () {
        	$modalInstance.close();
          };
    }
    
    angular
      .module('irida.cart', [])
      .service('CartService', ['$rootScope', '$http', '$q', CartService])
      .controller('CartSliderController', ['CartService','$modal', CartSliderController])
      .controller('GalaxyCartDialogCtrl',['$modalInstance','$timeout','GalaxyExportService','CartService',GalaxyCartDialogCtrl])
      .service('GalaxyExportService',[GalaxyExportService])
      .directive('cart', [CartDirective])
    ;
})();
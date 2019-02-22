import angular from "angular";
import find from "lodash/find";
import filter from "lodash/filter";
import { CART } from "../../utilities/events-utilities";
import $ from "jquery";
import { showNotification } from "../notifications";

function CartController(cart) {
  const vm = this;
  vm.show = false;
  vm.projects = [];
  vm.count = 0;
  vm.collapsed = {};
  vm.term = "";

  /*
  This is here since this has been updated to use a standard Event,
  and not handled through angularjs.
   */
  document.addEventListener(CART.UPDATED, function() {
    getCart(false);
  });

  function getCart(collapse) {
    cart.all().then(function(data) {
      vm.count = 0;
      vm.projects = data.projects;
      vm.projects.forEach(function(p) {
        vm.count += p.samples.length;
        // Sort the samples by created date.
        p.samples.sort(function(a, b) {
          return b.createdDate - a.createdDate > 0;
        });
        if (collapse) {
          vm.collapsed[p.id] = true;
        }
      });
    });
  }

  getCart(true);
}

/**
 * Controller for functions on the cart slider
 * @param CartService The cart service to communicate with the server
 */
function CartSliderController(CartService, $uibModal) {
  const vm = this;

  vm.clear = function() {
    CartService.clear();
  };

  vm.removeProject = function(projectId) {
    CartService.removeProject(projectId);
  };

  vm.removeSample = function(projectId, sampleId) {
    CartService.removeSample(projectId, sampleId);
  };

  vm.exportToGalaxy = function() {
    CartService.all().then(function(data) {
      if (data !== null) {
        const firstProjID = data.projects[0].id;

        $uibModal.open({
          templateUrl:
            window.TL.BASE_URL + "cart/template/galaxy/project/" + firstProjID,
          controller: "GalaxyDialogCtrl as gCtrl",
          resolve: {
            openedByCart: function() {
              return true;
            },
            multiProject: function() {
              return data.length > 1;
            },
            sampleIds: function() {
              return false;
            },
            projectId: function() {
              return false;
            }
          }
        });
      }
    });
  };
}

function CartDirective() {
  return {
    restrict: "E",
    templateUrl: "/cart.html",
    replace: true,
    controllerAs: "cart",
    controller: ["CartService", CartController]
  };
}

function CartService(scope, $http) {
  const svc = this;
  const urls = {
    all: window.TL.BASE_URL + "cart",
    add: window.TL.BASE_URL + "cart/add/samples",
    project: window.TL.BASE_URL + "cart/project/",
    galaxy: window.TL.BASE_URL + "cart/galaxy-export"
  };

  svc.all = function() {
    return $http.get(urls.all).then(function(response) {
      if (response.data) {
        return { projects: response.data.projects };
      } else {
        return [];
      }
    });
  };

  svc.galaxy = function() {
    return $http.get(urls.galaxy).then(function(response) {
      if (response.data) {
        return { projects: response.data.projects };
      } else {
        return [];
      }
    });
  };

  /*
  Add Samples to the global cart
  Event Listener for adding samples to the global cart.
  Expects an object with project ids references an array of sample ids
  { projectId: [sampleIds] }
   */
  document.addEventListener(
    CART.ADD,
    function(e) {
      const projects = e.detail.projects;
      if (typeof projects === "undefined") {
        return;
      }
      const promises = [];
      /*
    For each project that has samples, post the project/samples to
    and store the promise.
     */
      Object.keys(projects).forEach(projectId => {
        promises.push(
          $.post(window.TL.URLS.cart.add, {
            projectId,
            sampleIds: projects[projectId]
          }).then(response => {
            /*
          Display a notification of what occurred on the server.
           */
            const { message, excluded } = response;
            if (excluded) {
              showNotification({
                text: `
                    <p>${message}<p>
                    <ul>${excluded
                      .map(excludedSample => "<li>" + excludedSample + "</li>")
                      .join("")}</ul>`,
                progressBar: false,
                timeout: false,
                type: "warning"
              });
            } else {
              showNotification({
                text: message
              });
            }
          })
        );
      });

      /*
    Wait until all the projects have been added to the server cart, and
    then notify the UI that this has occurred.
     */
      $.when.apply($, promises).done(function() {
        const event = new Event(CART.UPDATED);
        document.dispatchEvent(event);
      });
    },
    false
  );

  svc.clear = function() {
    //fire a DELETE to the server on the cart then broadcast the cart update event
    return $http.delete(urls.all).then(function() {
      const event = new Event(CART.UPDATED);
      document.dispatchEvent(event);
    });
  };

  svc.removeProject = function(projectId) {
    return $http.delete(urls.project + projectId).then(function() {
      const event = new Event(CART.UPDATED);
      document.dispatchEvent(event);
    });
  };

  svc.removeSample = function(projectId, sampleId) {
    return $http
      .delete(urls.project + projectId + "/samples/" + sampleId)
      .then(function() {
        const event = new Event(CART.UPDATED);
        document.dispatchEvent(event);
      });
  };
}

function GalaxyExportService(CartService, $http, $q) {
  const svc = this,
    samples = [];

  function addSampleFile(sampleName, sampleFilePath) {
    let sample = find(samples, function(sampleItr) {
      return sampleItr.name === sampleName;
    });
    if (typeof sample === "undefined") {
      sample = {
        name: sampleName,
        _links: { self: { href: "" } }, //expected by the tool
        _embedded: { sample_files: [] }
      };
      samples.push(sample);
    }
    sample._embedded.sample_files.push({
      _links: { self: { href: sampleFilePath } }
    });
  }

  function getSampleFormEntities(args) {
    const defaults = {
        addtohistory: false,
        makepairedcollection: false
      },
      p = Object.assign({}, defaults, args),
      params = {
        _embedded: {
          library: { name: p.name },
          user: { email: p.email },
          addtohistory: p.addtohistory,
          makepairedcollection: p.makepairedcollection,
          oauth2: {
            code: p.authToken,
            redirect: p.redirectURI
          },
          samples: samples
        }
      };
    return [
      {
        name: "json_params",
        value: JSON.stringify(params)
      }
    ];
  }

  svc.exportFromProjSampPage = function(args, ids, projectId) {
    // Need to get an actual list of samples from the server from their ids.
    const promises = [];
    Object.keys(ids).map(function(id) {
      promises.push(
        $http({
          method: "POST",
          url: PAGE.urls.samples.idList,
          data: $.param({ sampleIds: ids[id], projectId: id }),
          headers: { "Content-Type": "application/x-www-form-urlencoded" }
        }).then(function(result) {
          result.data.samples.forEach(function(sample) {
            addSampleFile(sample.label, sample.href);
          });
        })
      );
    });
    return $q.all(promises).then(function() {
      return getSampleFormEntities(args);
    });
  };

  svc.exportFromCart = function(args) {
    return CartService.galaxy().then(function(data) {
      const projects = data.projects;
      projects.forEach(function(project) {
        const samples = project.samples;
        samples.forEach(function(sample) {
          addSampleFile(sample.label, sample.href);
        });
      });
      return getSampleFormEntities(args);
    });
  };
}

function GalaxyDialogCtrl(
  $uibModalInstance,
  $timeout,
  $scope,
  CartService,
  GalaxyExportService,
  openedByCart,
  multiProject,
  sampleIds,
  projectId
) {
  const vm = this;
  vm.addtohistory = true;
  vm.makepairedcollection = true;
  vm.showOauthIframe = false;
  vm.showEmailLibInput = true;
  vm.redirectURI = window.TL.BASE_URL + "galaxy/auth_code";
  vm.validation = {};

  vm.upload = function() {
    vm.validation = {};

    // Ensure email and name aren't empty
    if (vm.email === "") {
      vm.validation.email = true;
    }

    if (vm.name === "") {
      vm.validation.name = true;
    }

    // submit if no errors
    if (Object.keys(vm.validation).length === 0) {
      vm.makeOauth2AuthRequest(TL.galaxyClientID);
      vm.showEmailLibInput = false;
      vm.showOauthIframe = true;
    }
  };

  vm.makeOauth2AuthRequest = function(clientID) {
    const request = buildOauth2Request(
      clientID,
      "code",
      "read",
      vm.redirectURI
    );
    vm.iframeSrc = window.TL.BASE_URL + "api/oauth/authorize" + request;
  };

  function buildOauth2Request(clientID, responseType, scope, redirectURI) {
    return (
      "?&client_id=" +
      clientID +
      "&response_type=" +
      responseType +
      "&scope=" +
      scope +
      "&redirect_uri=" +
      redirectURI
    );
  }

  $scope.$on("galaxyAuthCode", function(e, authToken) {
    if (authToken) {
      vm.uploading = true;

      vm.showOauthIframe = false;
      vm.showEmailLibInput = true;

      if (openedByCart) {
        GalaxyExportService.exportFromCart({
          name: vm.name,
          email: vm.email,
          addtohistory: vm.addtohistory,
          makepairedcollection: vm.makepairedcollection,
          authToken: authToken,
          redirectURI: vm.redirectURI
        }).then(sendSampleForm);
      } else {
        GalaxyExportService.exportFromProjSampPage(
          {
            name: vm.name,
            email: vm.email,
            addtohistory: vm.addtohistory,
            makepairedcollection: vm.makepairedcollection,
            authToken: authToken,
            redirectURI: vm.redirectURI
          },
          sampleIds,
          projectId
        ).then(sendSampleForm);
      }
    }
  });

  function sendSampleForm(sampleFormEntities) {
    vm.sampleFormEntities = sampleFormEntities;

    if (openedByCart) {
      CartService.clear();
    }

    //$timeout is necessary because it adds a new event to the browser event queue,
    //allowing the full form to be generated before it is submitted.

    //Two timeouts are required now--this is starting to look like a real hack.
    $timeout(function() {
      $timeout(function() {
        document.getElementById("galSubFrm").submit();
        vm.close();
      });
    });
  }

  vm.setName = function(name, orgName) {
    if (multiProject) {
      vm.name = orgName;
    } else {
      vm.name = name;
    }
  };
  vm.setEmail = function(email) {
    vm.email = email;
  };
  vm.close = function() {
    $uibModalInstance.close();
  };
}

/**
 * @name cartFilter
 * @desc Filters the list of projects in the cart based on the term provided in the search input.
 * @type {Filter}
 */
function CartFilter() {
  /**
   * @param list - list to filter
   * @param term - search term to use to filter the list.
   */
  return function(list, term) {
    //filter each element in the collection
    return filter(list, function(item) {
      //if we have a project, check for how many samples we have left
      if (item.samples) {
        return filterList(item.samples, term).length > 0;
      }
      //if we have an individual sample, filter it
      return filterSample(item, term);
    });
  };

  function filterList(samples, term) {
    return filter(samples, function(s) {
      return filterSample(s, term);
    });
  }

  function filterSample(item, term) {
    term = term.toLowerCase();
    return item.label.toLowerCase().indexOf(term) > -1;
  }
}

angular
  .module("irida.cart", [])
  .service("CartService", ["$rootScope", "$http", CartService])
  .controller("CartSliderController", [
    "CartService",
    "$uibModal",
    CartSliderController
  ])
  .controller("GalaxyDialogCtrl", [
    "$uibModalInstance",
    "$timeout",
    "$scope",
    "CartService",
    "GalaxyExportService",
    "openedByCart",
    "multiProject",
    "sampleIds",
    "projectId",
    GalaxyDialogCtrl
  ])
  .service("GalaxyExportService", [
    "CartService",
    "$http",
    "$q",
    GalaxyExportService
  ])
  .directive("cart", [CartDirective])
  .filter("cartFilter", [CartFilter]);

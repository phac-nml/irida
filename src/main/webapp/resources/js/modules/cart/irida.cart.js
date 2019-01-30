import angular from "angular";
import find from "lodash/find";
import filter from "lodash/filter";
import { CART } from "../../utilities/events-utilities";
import { removeSample } from "../../apis/cart/cart";

function CartService(scope, $http) {
  const svc = this;
  const urls = {
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
    return removeSample(projectId, sampleId).then(detail => updateCart(detail));
  };
}

const updateCart = detail => {
  const event = new CustomEvent(CART.UPDATED, { detail });
  document.dispatchEvent(event);
};

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
  .filter("cartFilter", [CartFilter]);

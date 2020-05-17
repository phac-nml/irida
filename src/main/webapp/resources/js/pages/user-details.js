import $ from "jquery";
import angular from "angular";
import "angular-ui-bootstrap";
import { setBaseUrl } from "../utilities/url-utilities";

const module = angular.module("details", ["ui.bootstrap"]);

module.service("ResetService", [
  "$http",
  function($http) {
    const svc = this;

    svc.resetPassword = function() {
      return $http
        .get(setBaseUrl(`/password_reset/ajax/create/${window.PAGE.userId}`))
        .then(function({ data }) {
          if (data.success) {
            window.notifications.show({
              type: "success",
              text: data.message,
              title: data.title
            });
          } else {
            window.notifications.show({
              type: "error",
              text: data.message,
              title: data.title
            });
          }
          return data;
        });
    };
  }
]);

module.service("SubscriptionService", [
  "$http",
  function($http) {
    const svc = this;

    svc.updateSubscription = function(userId, projectId, subscribe) {
      const urlBase = setBaseUrl(
        `/events/projects/${projectId}/subscribe/${userId}`
      );

      return $http({
        method: "POST",
        url: urlBase,
        data: $.param({ subscribe }),
        headers: { "Content-Type": "application/x-www-form-urlencoded" }
      });
    };
  }
]);

module.controller("SubscriptionController", [
  "SubscriptionService",
  function(SubscriptionService) {
    const title = i18n("user.projects.subscriptions");
    const errorMessage = i18n("user.projects.subscriptions.error");

    angular.element(".subcription-checkbox").on("change", function() {
      const box = $(this);
      const projectId = box.val();
      let subscribe = false;
      if (box.is(":checked")) {
        subscribe = true;
      }

      SubscriptionService.updateSubscription(
        window.PAGE.userId,
        projectId,
        subscribe
      ).then(
        function({ data }) {
          window.notifications.show({
            type: "success",
            text: data.message,
            title: title
          });
        },
        function() {
          window.notifications.show({
            type: "error",
            text: errorMessage,
            title: title
          });
          box.attr("checked", false);
        }
      );
    });
  }
]);

module.controller("DetailsCtrl", [
  "$uibModal",
  function DetailsCtrl($uibModal) {
    "use strict";

    const vm = this;

    vm.resetPrompt = function() {
      $uibModal.open({
        templateUrl: "reset-modal.html",
        controller: "ResetCtrl as reset",
        size: "sm"
      });
    };
  }
]);

module.controller("ResetCtrl", [
  "$rootScope",
  "$uibModalInstance",
  "ResetService",
  function($rootScope, $uibModalInstance, ResetService) {
    "use strict";
    const vm = this;

    vm.close = function() {
      $uibModalInstance.close();
    };

    vm.reset = function() {
      ResetService.resetPassword().then(function() {
        vm.close();
      });
    };
  }
]);

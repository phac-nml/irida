/**
 * @file Loads the Events javascript onto the Admin Activities page.
 */
import angular from "angular";
import { EventsModule } from "../../modules/events/events";

angular.module("irida").requires.push(EventsModule);

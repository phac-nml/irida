import angular from "angular";
import { EventsModule } from "../modules/events/events";
import { DashboardAnnouncementsModule } from "./announcement/announcementDashboard";

angular.module("irida.dashboard", [EventsModule, DashboardAnnouncementsModule]);

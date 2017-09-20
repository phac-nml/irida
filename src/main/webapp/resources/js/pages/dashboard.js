import angular from "angular";
import { EventsModule } from "../modules/events/events";
import { DashboardAnnouncementsModule } from "./announcement/announcementDashboard";

const app = angular.module("irida");
app.requires.push(EventsModule);
app.requires.push(DashboardAnnouncementsModule);

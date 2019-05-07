import angular from "angular";
import { DashboardAnnouncementsModule } from "./announcementDashboard";

const app = angular.module("irida");
app.requires.push(DashboardAnnouncementsModule);

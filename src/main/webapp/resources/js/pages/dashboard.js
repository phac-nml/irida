import angular from "angular";
import { EventsModule } from "../modules/events/events";
import React from "react";
import { render } from "react-dom";

import { AnnouncementDashboard } from "./announcement/components/AnnouncementDashboard";

angular.module("irida.dashboard", [EventsModule]);

/**
 * Renders the React AnnouncementDashboard.
 * Responsible for displaying the announcements dashboard.
 */

render(<AnnouncementDashboard />, document.querySelector("#dashboard-root"));

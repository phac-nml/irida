import React from "react";
import { render } from "react-dom";

import { AnnouncementDashboard } from "./announcement/AnnouncementDashboard";

/**
 * Renders the React AnnouncementDashboard.
 * Responsible for displaying the announcements dashboard.
 */

render(<AnnouncementDashboard />, document.querySelector("#dashboard-root"));

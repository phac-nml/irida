import React from "react";
import { getUnreadAnnouncements } from "../../apis/announcements/announcements";

export function AnnouncementDashboard() {
  getUnreadAnnouncements().then((data) => {
    console.log(data);
  });
  return <h1>Hello, world!</h1>;
}

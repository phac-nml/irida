import React from "react";
import { VisibilityProvider } from "../../../contexts/visibility-context";
import ViewAnnouncementModal from "./ViewAnnouncementModal";

export default function ViewUnreadAnnouncement({
  announcementID,
  announcementTitle,
  markAnnouncementAsRead,
}) {
  return (
    <VisibilityProvider>
      <ViewAnnouncementModal
        announcementID={announcementID}
        announcementTitle={announcementTitle}
        markAnnouncementAsRead={markAnnouncementAsRead}
      />
    </VisibilityProvider>
  );
}

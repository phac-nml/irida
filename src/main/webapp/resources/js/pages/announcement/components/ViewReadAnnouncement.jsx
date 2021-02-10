import React from "react";
import { VisibilityProvider } from "../../../contexts/visibility-context";
import ViewAnnouncementModal from "./ViewAnnouncementModal";

export default function ViewReadAnnouncement({
  announcementID,
  announcementTitle,
}) {
  return (
    <VisibilityProvider>
      <ViewAnnouncementModal
        announcementID={announcementID}
        announcementTitle={announcementTitle}
      />
    </VisibilityProvider>
  );
}

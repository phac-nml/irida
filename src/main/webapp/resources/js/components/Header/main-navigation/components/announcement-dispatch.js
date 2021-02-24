import { markAnnouncementRead } from "../../../../apis/announcements/announcements";
import { TYPES } from "./announcements-context";

export function displayNextAnnouncement(dispatch, announcement) {
  markAnnouncementRead({ aID: announcement.identifier }).then(() => {
    dispatch({
      type: TYPES.SHOW_NEXT,
      payload: {
        announcement: { ...announcement, read: true },
      },
    });
  });
}

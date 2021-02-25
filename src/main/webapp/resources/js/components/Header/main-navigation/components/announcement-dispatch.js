import { markAnnouncementRead } from "../../../../apis/announcements/announcements";
import { TYPES } from "./announcements-context";

export function readAndNextAnnouncement(dispatch, announcement) {
  if (announcement.read) {
    dispatch({
      type: TYPES.READ_AND_NEXT,
      payload: {
        announcement: announcement,
      },
    });
  } else {
    markAnnouncementRead({ aID: announcement.identifier }).then(() => {
      dispatch({
        type: TYPES.READ_AND_NEXT,
        payload: {
          announcement: { ...announcement, read: true },
        },
      });
    });
  }
}

export function readAndPreviousAnnouncement(dispatch, announcement) {
  if (announcement.read) {
    dispatch({
      type: TYPES.READ_AND_PREVIOUS,
      payload: {
        announcement: announcement,
      },
    });
  } else {
    markAnnouncementRead({ aID: announcement.identifier }).then(() => {
      dispatch({
        type: TYPES.READ_AND_PREVIOUS,
        payload: {
          announcement: { ...announcement, read: true },
        },
      });
    });
  }
}

export function readAndCloseAnnouncement(dispatch, announcement) {
  if (announcement.read) {
    dispatch({
      type: TYPES.READ_AND_CLOSE,
      payload: {
        announcement: announcement,
      },
    });
  } else {
    markAnnouncementRead({ aID: announcement.identifier }).then(() => {
      dispatch({
        type: TYPES.READ_AND_CLOSE,
        payload: {
          announcement: { ...announcement, read: true },
        },
      });
    });
  }
}

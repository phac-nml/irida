import { markAnnouncementRead } from "../../../../apis/announcements/announcements";
import { TYPES } from "./announcements-context";

/**
 * Marks the announcement as read, if not already done so, and goes to the next announcement.
 *
 * @param {function} dispatch - triggers the next state change
 * @param {object} announcement - the announcement that is to be read
 */
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

/**
 * Marks the announcement as read, if not already done so, and goes to the previous announcement.
 *
 * @param {function} dispatch - triggers the next state change
 * @param {object} announcement - the announcement that is to be read
 */
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

/**
 * Marks the announcement as read, if not already done so, and closes the modal.
 *
 * @param {function} dispatch - triggers the next state change
 * @param {object} announcement - the announcement that is to be read
 */
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

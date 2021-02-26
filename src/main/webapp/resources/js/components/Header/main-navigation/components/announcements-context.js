import React from "react";
import { getUnreadAnnouncements } from "../../../../apis/announcements/announcements";

const AnnouncementContext = React.createContext();
AnnouncementContext.displayName = "Announcement Context";

export const TYPES = {
  LOADED: "ANNOUNCEMENTS_LOADED",
  SHOW_ANNOUNCEMENT: "SHOW_ANNOUNCEMENT",
  CLOSE_ANNOUNCEMENT: "CLOSE_ANNOUNCEMENT",
  READ_AND_NEXT: "READ_AND_NEXT",
  READ_AND_PREVIOUS: "READ_AND_PREVIOUS",
  READ_AND_CLOSE: "READ_AND_CLOSE",
};

const reducer = (state, action) => {
  switch (action.type) {
    case TYPES.LOADED:
      const isPriority =
        action.payload.announcements.filter((a) => a.priority).length > 0;
      return {
        ...state,
        announcements: action.payload.announcements,
        modalVisible: isPriority,
        index: 0,
        isPriority,
      };
    case TYPES.SHOW_ANNOUNCEMENT:
      return {
        ...state,
        modalVisible: true,
        index: action.payload.index,
        isPriority: action.payload.isPriority,
      };
    case TYPES.CLOSE_ANNOUNCEMENT:
      return {
        ...state,
        modalVisible: false,
        index: null,
        isPriority: null,
        announcements: state.announcements.filter((a) => !a.read),
      };
    case TYPES.READ_AND_NEXT:
      const newNextAnnouncements = [...state.announcements];
      newNextAnnouncements[state.index] = action.payload.announcement;
      return {
        ...state,
        index: state.index + 1,
        announcements: newNextAnnouncements,
      };
    case TYPES.READ_AND_PREVIOUS:
      const newPreviousAnnouncements = [...state.announcements];
      newPreviousAnnouncements[state.index] = action.payload.announcement;
      return {
        ...state,
        index: state.index - 1,
        announcements: newPreviousAnnouncements,
      };
    case TYPES.READ_AND_CLOSE:
      const newCloseAnnouncements = [...state.announcements];
      newCloseAnnouncements[state.index] = action.payload.announcement;
      return {
        ...state,
        modalVisible: false,
        index: null,
        isPriority: null,
        announcements: newCloseAnnouncements.filter((a) => !a.read),
      };
    default:
      return { ...state };
  }
};

function AnnouncementProvider({ children }) {
  const [state, dispatch] = React.useReducer(reducer, { announcements: [] });

  React.useEffect(() => {
    getUnreadAnnouncements().then((data) => {
      const announcements = data
        ? data.map((a) => ({
            ...a,
            id: `announcement-${a.identifier}`,
          }))
        : [];
      dispatch({
        type: TYPES.LOADED,
        payload: { announcements },
      });
    });
  }, []);

  const value = [state, dispatch];
  return (
    <AnnouncementContext.Provider value={value}>
      {children}
    </AnnouncementContext.Provider>
  );
}

function useAnnouncements() {
  const context = React.useContext(AnnouncementContext);
  if (context === undefined) {
    throw new Error(`useAnnouncements requires AnnouncementsProvider`);
  }
  return context;
}

export { AnnouncementProvider, useAnnouncements };

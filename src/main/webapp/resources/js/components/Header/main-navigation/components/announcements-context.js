import React from "react";
import { getUnreadAnnouncements } from "../../../../apis/announcements/announcements";

const AnnouncementContext = React.createContext();
AnnouncementContext.displayName = "Announcement Context";

export const TYPES = {
  LOADED: "ANNOUNCEMENTS_LOADED",
  SHOW_ANNOUNCEMENT: "SHOW_ANNOUNCEMENT",
  CLOSE_ANNOUNCEMENT: "CLOSE_ANNOUNCEMENT",
  SHOW_NEXT: "SHOW_NEXT",
};

const reducer = (state, action) => {
  switch (action.type) {
    case TYPES.LOADED:
      return { ...state, announcements: action.payload.announcements };
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
        announcements: state.announcements.filter((a) => a.read),
      };
    case TYPES.SHOW_NEXT:
      const newAnnouncements = [...state.announcements];
      newAnnouncements[state.index] = action.payload.announcement;
      return {
        ...state,
        index: state.index + 1,
        announcements: newAnnouncements,
      };
    default:
      return { ...state };
  }
};

function AnnouncementProvider({ children }) {
  const [state, dispatch] = React.useReducer(reducer, { announcements: [] });

  React.useEffect(() => {
    getUnreadAnnouncements({}).then(({ data }) => {
      const announcements = data.map((a) => ({
        ...a,
        id: `announcement-${a.identifier}`,
      }));
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

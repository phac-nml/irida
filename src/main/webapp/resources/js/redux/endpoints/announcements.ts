import { api } from "./api";
import { TAG_ANNOUNCEMENT_COUNT } from "./tags";

export const announcementsApi = api.injectEndpoints({
  endpoints: (build) => ({
    getAnnouncementCount: build.query({
      query: () => "announcements/count",
      providesTags: [TAG_ANNOUNCEMENT_COUNT],
    }),
  }),
});

export const { useGetAnnouncementCountQuery } = announcementsApi;

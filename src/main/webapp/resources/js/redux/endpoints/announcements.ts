import { api } from "./api";
import { TAG_ANNOUNCEMENT_COUNT, TAG_ANNOUNCEMENTS_UNREAD } from "./tags";
import { Announcement } from "../../types/irida";

/**
 * @fileoverview Announcement API for redux-toolkit.
 */

export const announcementsApi = api.injectEndpoints({
  endpoints: (build) => ({
    getAnnouncementCount: build.query({
      query: () => "announcements/count",
      providesTags: [TAG_ANNOUNCEMENT_COUNT],
    }),
    getUnreadHighPriorityAnnouncements: build.query<Announcement[], void>({
      query: () => "announcements/user/unread",
      transformResponse: (data: Announcement[]) =>
        data.filter((announcement) => announcement.priority),
      providesTags: (result = []) => [
        ...result.map(
          ({ id }: { id: number }) =>
            ({ type: TAG_ANNOUNCEMENTS_UNREAD, id } as const)
        ),
        { type: TAG_ANNOUNCEMENTS_UNREAD, id: "LIST" },
      ],
    }),
    markAnnouncementAsRead: build.mutation({
      query: (id: number) => ({
        url: `announcements/read/${id}`,
        method: "POST",
      }),
      invalidatesTags: [TAG_ANNOUNCEMENT_COUNT],
    }),
  }),
});

export const {
  useGetAnnouncementCountQuery,
  useGetUnreadHighPriorityAnnouncementsQuery,
  useMarkAnnouncementAsReadMutation,
} = announcementsApi;

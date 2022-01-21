import { configureStore } from "@reduxjs/toolkit";
import { usersApi } from "../../apis/users/users";
import { projectEventsApi } from "../../apis/projects/project-events";

/*
Redux Store for user details.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    [usersApi.reducerPath]: usersApi.reducer,
    [projectEventsApi.reducerPath]: projectEventsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(usersApi.middleware, projectEventsApi.middleware),
});

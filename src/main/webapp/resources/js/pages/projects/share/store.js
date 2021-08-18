import { configureStore } from "@reduxjs/toolkit";
import { projectsApi } from "../../../apis/projects/projects";

export default configureStore({
  reducer: {
    [projectsApi.reducerPath]: projectsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(projectsApi.middleware),
});

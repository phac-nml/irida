import { configureStore } from "@reduxjs/toolkit";
import { fieldsApi } from "../../../apis/metadata/field";
import { projectsApi } from "../../../apis/projects/projects";

export default configureStore({
  reducer: {
    [projectsApi.reducerPath]: projectsApi.reducer,
    [fieldsApi.reducerPath]: fieldsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(projectsApi.middleware, fieldsApi.middleware),
});

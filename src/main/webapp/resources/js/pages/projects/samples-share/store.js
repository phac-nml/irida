import { configureStore } from "@reduxjs/toolkit";
import { fieldsApi } from "../../../apis/metadata/field";
import { projectApi } from "../../../apis/projects/project";
import { projectsApi } from "../../../apis/projects/projects";
import shareReducer from "./services/shareReducer";

export default configureStore({
  reducer: {
    [projectApi.reducerPath]: projectApi.reducer,
    [projectsApi.reducerPath]: projectsApi.reducer,
    [fieldsApi.reducerPath]: fieldsApi.reducer,
    reducer: shareReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      projectApi.middleware,
      projectsApi.middleware,
      fieldsApi.middleware
    ),
});

import { configureStore } from "@reduxjs/toolkit";
import { fieldsApi } from "../../../apis/metadata/field";
import { projectApi } from "../../../apis/projects/project";
import { projectsApi } from "../../../apis/projects/projects";
import { rootReducer } from "./services/rootReducer";

export default configureStore({
  reducer: {
    [projectApi.reducerPath]: projectApi.reducer,
    [projectsApi.reducerPath]: projectsApi.reducer,
    [fieldsApi.reducerPath]: fieldsApi.reducer,
    reducer: rootReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      projectApi.middleware,
      projectsApi.middleware,
      fieldsApi.middleware
    ),
});

import { configureStore } from "@reduxjs/toolkit";
import { fieldsApi } from "../../../apis/metadata/field";
import { projectsApi } from "../../../apis/projects/projects";
import { samplesApi } from "../../../apis/projects/samples";
import shareReducer from "./shareSlice";

export default configureStore({
  reducer: {
    shareReducer,
    [projectsApi.reducerPath]: projectsApi.reducer,
    [samplesApi.reducerPath]: samplesApi.reducer,
    [fieldsApi.reducerPath]: fieldsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      projectsApi.middleware,
      samplesApi.middleware,
      fieldsApi.middleware
    ),
});

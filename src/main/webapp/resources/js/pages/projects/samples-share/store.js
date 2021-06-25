import { configureStore } from "@reduxjs/toolkit";
import { fieldsApi } from "../../../apis/metadata/field";
import { projectsApi } from "../../../apis/projects/projects";
import { rootReducer } from "./services/rootReducer";

export default configureStore({
  reducer: {
    [projectsApi.reducerPath]: projectsApi.reducer,
    [fieldsApi.reducerPath]: fieldsApi.reducer,
    reducer: rootReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(projectsApi.middleware, fieldsApi.middleware),
});

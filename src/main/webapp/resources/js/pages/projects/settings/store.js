import { configureStore } from "@reduxjs/toolkit";
import { fieldsApi } from "../../../apis/metadata/field";
import { templateApi } from "../../../apis/metadata/metadata-templates";
import { associatedProjectsApi } from "../../../apis/projects/associated-projects";

import { projectApi } from "../../../apis/projects/project";
import pipelineReducer from "../redux/pipelinesSlice";
import userReducer from "../redux/userSlice";

/*
Redux store for project metadata.
For more information on redux stores see: https://redux-toolkit.js.org/api/configureStore
 */
export default configureStore({
  reducer: {
    pipelines: pipelineReducer,
    user: userReducer,
    [projectApi.reducerPath]: projectApi.reducer,
    [templateApi.reducerPath]: templateApi.reducer,
    [fieldsApi.reducerPath]: fieldsApi.reducer,
    [associatedProjectsApi.reducerPath]: associatedProjectsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      projectApi.middleware,
      templateApi.middleware,
      fieldsApi.middleware,
      associatedProjectsApi.middleware
    ),
});

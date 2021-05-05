import { configureStore } from "@reduxjs/toolkit";
import { fieldsApi } from "../../../apis/metadata/field";
import { templateApi } from "../../../apis/metadata/metadata-templates";
import { associatedProjectsApi } from "../../../apis/projects/associated-projects";

import { projectApi } from "../../../apis/projects/project";
import coverageReducer from "../redux/coverageSlice";
import pipelineReducer from "../redux/pipelinesSlice";
import projectReducer from "../redux/projectSlice";
import userReducer from "../redux/userSlice";

/*
Redux Store for project metadata.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    project: projectReducer,
    coverage: coverageReducer,
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

import { configureStore } from "@reduxjs/toolkit";

import { templateApi } from "../../../apis/metadata/metadata-templates";
import coverageReducer from "../redux/coverageSlice";
import fieldsReducer from "../redux/fieldsSlice";
import pipelineReducer from "../redux/pipelinesSlice";
import projectReducer from "../redux/projectSlice";
import templateReducer from "../redux/templatesSlice";
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
    fields: fieldsReducer,
    templates: templateReducer,
    [templateApi.reducerPath]: templateApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(templateApi.middleware),
});

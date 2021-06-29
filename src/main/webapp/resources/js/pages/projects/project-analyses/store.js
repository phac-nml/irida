import { configureStore } from "@reduxjs/toolkit";
import { projectApi } from "../../../apis/projects/project";
import { singleSampleAnalysisOutputsApi } from "../../../apis/projects/analyses";

/*
Redux Store for project details and project analyses outputs.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    [projectApi.reducerPath]: projectApi.reducer,
    [singleSampleAnalysisOutputsApi.reducerPath]:
      singleSampleAnalysisOutputsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      projectApi.middleware,
      singleSampleAnalysisOutputsApi.middleware
    ),
});

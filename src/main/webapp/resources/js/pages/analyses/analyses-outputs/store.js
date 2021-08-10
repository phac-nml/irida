import { configureStore } from "@reduxjs/toolkit";
import { singleSampleAnalysisOutputsApi } from "../../../apis/analyses/analyses";

/*
Redux Store for project details and project analyses outputs.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    [singleSampleAnalysisOutputsApi.reducerPath]:
      singleSampleAnalysisOutputsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(singleSampleAnalysisOutputsApi.middleware),
});

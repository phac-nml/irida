import { configureStore } from "@reduxjs/toolkit";
import { sequencingRunsApi } from "../../apis/sequencing-runs/sequencing-runs";
import fastQCReducer from "../../components/samples/components/fastqc/fastQCSlice";

/*
Redux Store for sequencing runs.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    fastQCReducer,
    [sequencingRunsApi.reducerPath]: sequencingRunsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(sequencingRunsApi.middleware),
});

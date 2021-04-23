import { configureStore } from "@reduxjs/toolkit";
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
  },
});

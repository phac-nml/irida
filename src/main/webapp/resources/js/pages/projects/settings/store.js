import { configureStore } from "@reduxjs/toolkit";
import coverageSlice from "../redux/coverageSlice";
import membersSlice from "../redux/membersSlice";
import pipelinesSlice from "../redux/pipelinesSlice";
import projectSlice from "../redux/projectSlice";
import userSlice from "../redux/userSlice";

/*
Redux Store for project metadata.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    project: projectSlice,
    coverage: coverageSlice,
    pipelines: pipelinesSlice,
    members: membersSlice,
    user: userSlice,
  },
});

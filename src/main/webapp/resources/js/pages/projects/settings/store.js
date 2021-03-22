import { configureStore } from "@reduxjs/toolkit";
import projectReducer from "../redux/projectSlice";

/*
Redux Store for project metadata.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    project: projectReducer,
  },
});

import { configureStore } from "@reduxjs/toolkit";
import fieldsReducer from "../redux/fieldsSlice";
import templatesReducer from "../redux/templatesSlice";
import projectReducer from "../redux/projectSlice";

/*
Redux Store for project metadata.
For more information on redux stores, please see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    fields: fieldsReducer,
    templates: templatesReducer,
    project: projectReducer,
  },
});

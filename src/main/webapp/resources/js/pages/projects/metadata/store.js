import { configureStore } from "@reduxjs/toolkit";
import fieldsReducer from "../redux/fieldsSlice";
import templatesReducer from "../redux/templatesSlice";
import projectReducer from "../redux/projectSlice";

export default configureStore({
  reducer: {
    fields: fieldsReducer,
    templates: templatesReducer,
    project: projectReducer,
  },
});

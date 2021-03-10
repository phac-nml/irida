import { configureStore } from "@reduxjs/toolkit";
import fieldsReducer from "../fields/fieldsSlice";
import templatesReducer from "../templates/templatesSlice";

export default configureStore({
  reducer: {
    fields: fieldsReducer,
    templates: templatesReducer,
  },
});

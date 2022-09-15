import { configureStore } from "@reduxjs/toolkit";
import { metadataImportApi } from "../../../apis/metadata/metadata-import";
import { importReducer } from "./services/importReducer";

/*
Redux Store for sample metadata importer.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    importReducer,
    [metadataImportApi.reducerPath]: metadataImportApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(metadataImportApi.middleware),
});

import { configureStore } from "@reduxjs/toolkit";
import { metadataImportApi } from "../../../apis/metadata/metadata-import";
import { rootReducer } from "./services/rootReducer";

export default configureStore({
  reducer: {
    reducer: rootReducer,
    [metadataImportApi.reducerPath]: metadataImportApi.reducer,
  },
});

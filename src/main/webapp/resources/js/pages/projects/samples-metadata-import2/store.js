import { configureStore } from "@reduxjs/toolkit";
import { metadataImportApi } from "../../../apis/metadata/metadata-import";

export default configureStore({
  reducer: {
    [metadataImportApi.reducerPath]: metadataImportApi.reducer,
  },
});

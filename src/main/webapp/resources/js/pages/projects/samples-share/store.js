import { configureStore } from "@reduxjs/toolkit";

export default configureStore({
  reducer: {},
  middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(),
});

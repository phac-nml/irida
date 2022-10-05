import { configureStore } from "@reduxjs/toolkit";
import { importReducer } from "./services/importReducer";
import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux";

/*
Redux Store for sample metadata importer.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
const store = configureStore({
  reducer: {
    importReducer,
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware(),
});

export type ImportState = ReturnType<typeof store.getState>;
export type ImportDispatch = typeof store.dispatch;
export const useImportDispatch: () => ImportDispatch = useDispatch;
export const useImportSelector: TypedUseSelectorHook<ImportState> = useSelector;

export default store;

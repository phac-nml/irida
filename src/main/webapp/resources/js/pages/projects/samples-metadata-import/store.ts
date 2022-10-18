import {
  Action,
  configureStore,
  Dispatch,
  MiddlewareAPI,
} from "@reduxjs/toolkit";
import { importReducer } from "./services/importReducer";
import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";

const storageKey = "metadataImport";

const storeState = (store: MiddlewareAPI) => {
  return (next: Dispatch) => (action: Action) => {
    const result = next(action);
    sessionStorage.setItem(storageKey, JSON.stringify(store.getState()));
    return result;
  };
};

const retrieveState = () => {
  const stringData = sessionStorage.getItem(storageKey);
  if (stringData != null) {
    const projectIdFromUrl = getProjectIdFromUrl();
    const result = JSON.parse(stringData);
    const projectIdFromStorage = result.importReducer.projectId;
    if (projectIdFromUrl === projectIdFromStorage) {
      return result;
    }
  }
};

/*
Redux Store for sample metadata importer.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
const store = configureStore({
  reducer: {
    importReducer,
  },
  preloadedState: retrieveState(),
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(storeState),
});

export type ImportState = ReturnType<typeof store.getState>;
export type ImportDispatch = typeof store.dispatch;
export const useImportDispatch: () => ImportDispatch = useDispatch;
export const useImportSelector: TypedUseSelectorHook<ImportState> = useSelector;

export default store;

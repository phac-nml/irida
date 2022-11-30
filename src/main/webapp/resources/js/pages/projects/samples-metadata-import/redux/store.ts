import {
  Action,
  configureStore,
  Dispatch,
  MiddlewareAPI,
} from "@reduxjs/toolkit";
import { importReducer } from "./importReducer";
import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux";
import { getProjectIdFromUrl } from "../../../../utilities/url-utilities";

const projectId = getProjectIdFromUrl();
const storageKey = "metadataImport-" + projectId;

const storeState = (store: MiddlewareAPI) => {
  return (next: Dispatch) => (action: Action) => {
    const result = next(action);
    const expirationDate = new Date(new Date().getTime() + 6 * 60 * 60 * 1000);
    const storage = {
      state: store.getState(),
      expiry: expirationDate.toISOString(),
    };
    sessionStorage.setItem(storageKey, JSON.stringify(storage));
    return result;
  };
};

const retrieveState = () => {
  const stringData = sessionStorage.getItem(storageKey);
  if (stringData !== null) {
    const result = JSON.parse(stringData);
    const expiry = new Date(result.expiry);
    if (expiry > new Date()) {
      return result.state;
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

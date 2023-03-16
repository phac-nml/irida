import { configureStore } from "@reduxjs/toolkit";
import { fieldsApi } from "../../../apis/metadata/field";
import { projectsApi } from "../../../apis/projects/projects";
import { samplesApi } from "../../../apis/projects/samples";
import shareReducer from "./shareSlice";
import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux";

const store = configureStore({
  reducer: {
    shareReducer,
    [projectsApi.reducerPath]: projectsApi.reducer,
    [samplesApi.reducerPath]: samplesApi.reducer,
    [fieldsApi.reducerPath]: fieldsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      projectsApi.middleware,
      samplesApi.middleware,
      fieldsApi.middleware
    ),
});

export type ShareState = ReturnType<typeof store.getState>;
export type ShareDispatch = typeof store.dispatch;
export const useShareDispatch: () => ShareDispatch = useDispatch;
export const useShareSelector: TypedUseSelectorHook<ShareState> = useSelector;

export default store;

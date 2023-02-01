import { configureStore } from "@reduxjs/toolkit";
import type { TypedUseSelectorHook } from "react-redux";
import { useSelector } from "react-redux";
import { associatedProjectsApi } from "../../../apis/projects/associated-projects";
import { projectApi } from "../../../apis/projects/project";
import { samplesApi } from "../../../apis/projects/samples";
import samplesReducer from "../redux/samplesSlice";

/**
 * Redux store for project samples
 */
const store = configureStore({
  reducer: {
    samples: samplesReducer,
    [projectApi.reducerPath]: projectApi.reducer,
    [samplesApi.reducerPath]: samplesApi.reducer,
    [associatedProjectsApi.reducerPath]: associatedProjectsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      samplesApi.middleware,
      projectApi.middleware,
      associatedProjectsApi.middleware
    ),
  devTools: process.env.NODE_ENV !== "production",
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
export const useAppDispatch = () => store.dispatch;
export const useTypedSelector: TypedUseSelectorHook<RootState> = useSelector;

export default store;

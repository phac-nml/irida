/**
 * @file Base file for the project samples page.
 */
import React from "react";
import { createRoot } from 'react-dom/client';
import ProjectSamples from "./components/ProjectSamples";
import { configureStore } from "@reduxjs/toolkit";
import { setupListeners } from "@reduxjs/toolkit/query";
import { Provider } from "react-redux";
import { samplesApi } from "../../../apis/projects/samples";
import { associatedProjectsApi } from "../../../apis/projects/associated-projects";
import samplesReducer from "../redux/samplesSlice";
import { projectApi } from "../../../apis/projects/project";
import { setBaseUrl } from "../../../utilities/url-utilities";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

/**
 * Redux store for project samples
 */
export const store = configureStore({
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
setupListeners(store.dispatch);

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <Provider store={store}>
    <ProjectSamples />
  </Provider>
);

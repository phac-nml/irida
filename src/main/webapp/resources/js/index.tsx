import React from "react";
import { render } from "react-dom";
import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
} from "react-router-dom";
import { Provider } from "react-redux";
import { store } from "./redux/store";
import AppLayout from "./layouts/app-layout";
import ProjectLayout, { projectLoader } from "./layouts/project-layout";
import PageBoundary from "./layouts/error-boundary/PageBoundary";
import { CONTEXT_PATH } from "./data/routes";

const ProjectSamples = React.lazy(() => import("./layouts/project-samples"));

/**
 * @fileoverview This is the highest level React component in IRIDA.  it's responsible
 * for the global layout, and routing.
 */

__webpack_public_path__ = `${CONTEXT_PATH}/dist/`;

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route path={CONTEXT_PATH} element={<AppLayout />}>
      <Route
        path={`projects/:projectId`}
        element={<ProjectLayout />}
        loader={projectLoader}
        errorElement={<PageBoundary />}
      >
        <Route index element={<ProjectSamples />} id={`project-samples`} />
        <Route
          path={`linelist`}
          element={<div>LINELIST</div>}
          id={`project-linelist`}
        />
        <Route
          path={`analyses`}
          element={<div>ANALYSES</div>}
          id={`project-analyses`}
        />
        <Route
          path={`exports`}
          element={<div>EXPORTS</div>}
          id={`project-exports`}
        />
        <Route
          path={`activity`}
          element={<div>ACTIVITY</div>}
          id={`project-activity`}
        />
        <Route
          path={`settings`}
          element={<div>SETTINGS</div>}
          id={`project-settings`}
        />
        {/*<Route*/}
        {/*  path="ncbi"*/}
        {/*  element={<NcbiCreateExport />}*/}
        {/*  loader={ncbiLoader}*/}
        {/*  errorElement={<DefaultErrorBoundary />}*/}
        {/*/>*/}
        {/*<Route*/}
        {/*  path="export"*/}
        {/*  element={<ProjectNCBILayout />}*/}
        {/*  errorElement={<DefaultErrorBoundary />}*/}
        {/*>*/}
        {/*  <Route*/}
        {/*    index*/}
        {/*    element={<NcbiExportTable />}*/}
        {/*    loader={exportsLoader}*/}
        {/*    errorElement={<DefaultErrorBoundary />}*/}
        {/*  />*/}
        {/*  <Route*/}
        {/*    path=":id"*/}
        {/*    element={<NCBIExportDetails />}*/}
        {/*    loader={detailsLoader}*/}
        {/*    errorElement={<DefaultErrorBoundary />}*/}
        {/*  />*/}
        {/*</Route>*/}
      </Route>
    </Route>
  )
);

render(
  <Provider store={store}>
    <RouterProvider router={router} />
  </Provider>,
  document.querySelector("#root")
);

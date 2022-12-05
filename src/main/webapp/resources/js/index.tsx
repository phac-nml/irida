import React, { Suspense } from "react";
import { render } from "react-dom";
import {
  createBrowserRouter,
  createRoutesFromElements,
  Outlet,
  Route,
  RouterProvider,
} from "react-router-dom";
import { Provider } from "react-redux";
import { store } from "./redux/store";
import { getContextPath } from "./utilities/url-utilities";
import { Layout } from "antd";
import MainNavigation from "./components/main-navigation";
import { LoadingOutlined } from "@ant-design/icons";
import AppLayout from "./layouts/app-layout";

/**
 * @fileoverview This is the highest level React component in IRIDA.  it's responsible
 * for the global layout, and routing.
 */

const CONTEXT_PATH = getContextPath();

__webpack_public_path__ = `${CONTEXT_PATH}/dist/`;

// TODO: (Josh - 12/2/22) Build up from the root here so we can easy add
const router = createBrowserRouter(
  createRoutesFromElements(
    <Route path={CONTEXT_PATH} element={<AppLayout />}>
      <Route path={`projects/:projectId`} element={<div>SAMPLES</div>}>
        <Route index element={<div>SAMPLES</div>} />
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

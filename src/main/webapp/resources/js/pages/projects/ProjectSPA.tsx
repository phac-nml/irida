import React, { Suspense } from "react";
import { render } from "react-dom";
import {
  createBrowserRouter,
  createRoutesFromElements,
  Outlet,
  Route,
  RouterProvider,
} from "react-router-dom";
import MainNavigation from "../../components/main-navigation";
import { loader as exportsLoader } from "../../components/ncbi/export-table";
import { getContextPath } from "../../utilities/url-utilities";
import { loader as detailsLoader } from "../../components/ncbi/details";
import { LoadingOutlined } from "@ant-design/icons";
import { loader as ncbiLoader } from "./ncbi/create";
import { Layout } from "antd";
import { Provider } from "react-redux";
import { store } from "../../redux/store";

const ProjectNCBILayout = React.lazy(() => import("./ncbi"));
const NCBIExportDetails = React.lazy(
  () => import("../../components/ncbi/details")
);
const NcbiExportTable = React.lazy(
  () => import("../../components/ncbi/export-table")
);
const NcbiCreateExport = React.lazy(() => import("./ncbi/create"));
const DefaultErrorBoundary = React.lazy(
  () => import("../../components/DefaultErrorBoundary")
);

const CONTEXT_PATH = getContextPath();

__webpack_public_path__ = `${CONTEXT_PATH}/dist/`;

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route
      path={`${CONTEXT_PATH}/projects/:projectId`}
      element={<ProjectBase />}
    >
      <Route index element={<div>SAMPLES</div>} />
      <Route
        path="ncbi"
        element={<NcbiCreateExport />}
        loader={ncbiLoader}
        errorElement={<DefaultErrorBoundary />}
      />
      <Route
        path="export"
        element={<ProjectNCBILayout />}
        errorElement={<DefaultErrorBoundary />}
      >
        <Route
          index
          element={<NcbiExportTable />}
          loader={exportsLoader}
          errorElement={<DefaultErrorBoundary />}
        />
        <Route
          path=":id"
          element={<NCBIExportDetails />}
          loader={detailsLoader}
          errorElement={<DefaultErrorBoundary />}
        />
      </Route>
    </Route>
  )
);

/**
 * Default layout for the Project Single Page Application
 * @constructor
 */
function ProjectBase(): JSX.Element {
  return (
    <Layout style={{ height: `100vh` }}>
      <Layout.Header>
        <MainNavigation />
      </Layout.Header>
      <Layout.Content>
        <Suspense fallback={<LoadingOutlined />}>
          <Outlet />
        </Suspense>
      </Layout.Content>
    </Layout>
  );
}

/**
 * Base component for the Project SPA.
 *  - Routes
 *  - Any Redux store should be added here
 * @constructor
 */
export default function ProjectSPA(): JSX.Element {
  return (
    <Provider store={store}>
      <RouterProvider router={router} />
    </Provider>
  );
}

render(<ProjectSPA />, document.querySelector("#root"));

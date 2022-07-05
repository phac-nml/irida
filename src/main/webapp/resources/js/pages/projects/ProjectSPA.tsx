import React, {Suspense} from "react";
import {DataBrowserRouter, Outlet, Route} from "react-router-dom";
import {loader as exportsLoader,} from "../../components/ncbi/export-table";
import {setBaseUrl} from "../../utilities/url-utilities";
import ProjectNCBILayout from "./ncbi";
import {loader as detailsLoader,} from "../../components/ncbi/details";
import {LoadingOutlined} from "@ant-design/icons";


const NCBIExportDetails = React.lazy(() => import("../../components/ncbi/details"));
const NcbiExportTable = React.lazy(() => import("../../components/ncbi/export-table"));
const DefaultErrorBoundary = React.lazy(() => import ("../../components/DefaultErrorBoundary"));

__webpack_public_path__ = setBaseUrl(`/dist/`);

/**
 * Default layout for the Project Single Page Application
 * @constructor
 */
function ProjectBase(): JSX.Element {
  return (
    <div>
      {/* TODO: NAV AND OTHER TOP LEVEL ITEMS HERE */}
        <Suspense fallback={<LoadingOutlined />}><Outlet/></Suspense>
    </div>
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
    <DataBrowserRouter>
      <Route
        path={setBaseUrl(`/projects/:projectId`)}
        element={<ProjectBase />}
      >
        <Route path="export" element={<ProjectNCBILayout />}>
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
    </DataBrowserRouter>
  );
}

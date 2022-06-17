import React from "react";
import {DataBrowserRouter, Outlet, Route} from "react-router-dom";
import NcbiExportTable, {loader as exportsLoader,} from "../../components/ncbi/export-table";
import {setBaseUrl} from "../../utilities/url-utilities";
import ProjectNCBILayout from "./ncbi";
import NCBIExportDetails, {loader as detailsLoader,} from "../../components/ncbi/details";
import DefaultErrorBoundary from "../../components/DefaultErrorBoundary";

__webpack_public_path__ = setBaseUrl(`/dist/`);

/**
 * Default layout for the Project Single Page Application
 * @constructor
 */
function ProjectBase(): JSX.Element {
  return (
    <div>
      {/* TODO: NAV AND OTHER TOP LEVEL ITEMS HERE */}
      <Outlet />
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

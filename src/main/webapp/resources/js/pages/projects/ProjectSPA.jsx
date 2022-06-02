import React from "react";
import { BrowserRouter, Outlet, Route, Routes } from "react-router-dom";
import { NcbiExportTable } from "../../components/ncbi/export-table/NcbiExportTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import ProjectNCBILayout from "./ncbi";
import NCBIExportDetails from "./ncbi/details";

function ProjectBase() {
  return (
    <div>
      {/* TODO: NAV AND STUFF HERE */}
      <Outlet />
    </div>
  );
}

export default function ProjectSPA() {
  return (
    <BrowserRouter basename={setBaseUrl(`/projects/`)}>
      <Routes>
        <Route path=":projectId" element={<ProjectBase />}>
          <Route path="export" element={<ProjectNCBILayout />}>
            <Route index element={<NcbiExportTable />} />
            <Route path=":id" element={<NCBIExportDetails />} />
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

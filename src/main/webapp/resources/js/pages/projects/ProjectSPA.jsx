import React from "react";
import { BrowserRouter, Outlet, Route, Routes } from "react-router-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import NCBIExportDetails from "./ncbi-details/index";
import { ProjectNcbiExportsPage } from "./ncbi-export/index";

function ProjectBase() {
  return (
    <div>
      {/* NAV AND STUFF HERE */}
      <Outlet />
    </div>
  );
}

export default function ProjectSPA({}) {
  return (
    <BrowserRouter basename={setBaseUrl(`/projects/`)}>
      <Routes>
        <Route path=":projectId" element={<ProjectBase />}>
          <Route path="export" element={<ProjectNcbiExportsPage />} />
          <Route path="export/:id" element={<NCBIExportDetails />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

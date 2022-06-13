import React from "react";
import {DataBrowserRouter, Outlet, Route} from "react-router-dom";
import NcbiExportTable, {loader as exportsLoader} from "../../components/ncbi/export-table/NcbiExportTable";
import {setBaseUrl} from "../../utilities/url-utilities";
import ProjectNCBILayout from "./ncbi";
import NCBIExportDetails, {loader as detailsLoader} from "./ncbi/details";


function ProjectBase(): JSX.Element {
    return (
        <div>
            {/* TODO: NAV AND STUFF HERE */}
            <Outlet/>
        </div>
    );
}

export default function ProjectSPA(): JSX.Element {
    return (
        <DataBrowserRouter>
            <Route path={setBaseUrl(`/projects/:projectId`)} element={<ProjectBase/>}>
                <Route path="export" element={<ProjectNCBILayout/>}>
                    <Route index element={<NcbiExportTable/>} loader={exportsLoader} errorElement={<div>FUCK ME, I BROKE!</div>}/>
                    <Route path=":id" element={<NCBIExportDetails/>} loader={detailsLoader}/>
                </Route>
            </Route>
        </DataBrowserRouter>
    );
}
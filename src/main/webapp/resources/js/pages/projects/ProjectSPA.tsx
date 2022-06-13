import React from "react";
import {DataBrowserRouter, Outlet, Route} from "react-router-dom";
import {NcbiExportTable} from "../../components/ncbi/export-table/NcbiExportTable";
import {setBaseUrl} from "../../utilities/url-utilities";
import ProjectNCBILayout from "./ncbi";
import NCBIExportDetails from "./ncbi/details";
import {getNcbiSubmission, getProjectNCBIExports} from "../../apis/export/ncbi";
import {NcbiBioSampleFiles, NcbiSubmission} from "../../types/irida";
import {formatNcbiUploadDetails, formatNcbiUploadFiles} from "./ncbi/details/utils";
import {BasicListItem} from "../../components/lists/BasicList.types";

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
                    <Route index element={<NcbiExportTable/>} loader={({params}) => {
                        if (params.projectId) {
                            return getProjectNCBIExports(parseInt(params.projectId));
                        } else {
                            return Promise.reject("Requires a project id");
                        }
                    }}/>
                    <Route path=":id" element={<NCBIExportDetails/>} loader={({params}) => {
                        const {id, projectId} = params;
                        if (projectId && id) {
                            return getNcbiSubmission(parseInt(projectId), parseInt(id))
                                .then((submission: NcbiSubmission): [BasicListItem[], NcbiBioSampleFiles[]] => {
                                    const { bioSampleFiles, ...info } = submission;
                                    const details = formatNcbiUploadDetails(info);
                                    const bioSamples = formatNcbiUploadFiles(bioSampleFiles);
                                    return [details, bioSamples];
                                })
                        } else {
                            return Promise.reject("No project id or export id");
                        }
                    }}/>
                </Route>
            </Route>
        </DataBrowserRouter>
    );
}
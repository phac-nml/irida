import React from "react";
import SamplesTable from "../../components/project/samples-table/SamplesTable";
import { ProjectSamplesProvider } from "../../components/project/samples-table/useProjectSamplesContext";

/**
 * React component to render the layout for the project > samples pages
 * @constructor
 */
export default function ProjectSamplesLayout(): JSX.Element {
  return (
    <ProjectSamplesProvider>
      <SamplesTable />
    </ProjectSamplesProvider>
  );
}

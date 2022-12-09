import React from "react";
import SamplesTable from "../components/project/samples-table/samples-table";

/**
 * React component to render the layout for the project > samples pages
 * @constructor
 */
export default function ProjectSamplesLayout(): JSX.Element {
  return (
    <div>
      <SamplesTable />
    </div>
  );
}

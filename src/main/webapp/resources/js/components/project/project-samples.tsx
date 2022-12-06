import React from "react";
import { useParams } from "react-router-dom";
import { useGetProjectDetailsQuery } from "../../redux/endpoints/project";
import { SamplesTable } from "../../pages/projects/samples/components/SamplesTable";

export default function ProjectSamples() {
  const { projectId } = useParams();
  const { data: details } = useGetProjectDetailsQuery(projectId);
  console.log(details);

  return (
    <div>
      SAMPLES SHIT
      {/*<SamplesTable />*/}
    </div>
  );
}

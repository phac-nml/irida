import React from "react";
import { Outlet, useParams } from "react-router-dom";
import { PageHeader } from "antd";
import { useGetProjectDetailsQuery } from "../redux/endpoints/project";
import ProjectNavigation from "../components/project/project-navigation";

/**
 * React component for the layout of the project specific pages
 * @constructor
 */
export default function ProjectLayout(): JSX.Element {
  const { projectId } = useParams();

  const { data: details = {} } = useGetProjectDetailsQuery(projectId);

  return (
    <PageHeader
      title={details.label}
      subTitle={details.description}
      style={{ margin: `0 25px` }}
    >
      <div style={{ backgroundColor: `#ffffff` }}>
        <ProjectNavigation />
        <div style={{ padding: 20 }}>
          <Outlet />
        </div>
      </div>
    </PageHeader>
  );
}

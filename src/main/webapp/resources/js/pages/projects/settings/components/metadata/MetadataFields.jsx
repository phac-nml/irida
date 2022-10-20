import React from "react";
import { useParams } from "react-router-dom";
import { useGetProjectDetailsQuery } from "../../../../../apis/projects/project";

import MetadataFieldsListManager from "./MetadataFieldsListManager";
import MetadataFieldsListMember from "./MetadataFieldsListMember";
import { Typography } from "antd";

/**
 * React component to determine if the user can manage the project,
 * if so return the manager component, otherwise return the member component
 * @returns React.Component
 */
export default function MetadataFields() {
  const { projectId } = useParams();

  const { data: project = {} } = useGetProjectDetailsQuery(projectId, {
    skip: !projectId,
  });

  return (
    <>
      <Typography.Title level={2}>
        {i18n("MetadataFields.title")}
      </Typography.Title>
      {project.canManage ? (
        <MetadataFieldsListManager />
      ) : (
        <MetadataFieldsListMember />
      )}
    </>
  );
}

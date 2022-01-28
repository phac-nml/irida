import React from "react";
import { useParams } from "react-router-dom";
import { useGetProjectDetailsQuery } from "../../../../../apis/projects/project";
import MetadataTemplateManager from "./MetadataTemplateManager";
import MetadataTemplateMember from "./MetadataTemplateMember";

/**
 * React component to determine if the user can manage the project,
 * if so return the maanger component, otherwise return the member component
 * @returns React.Component
 */
export default function MetadataTemplate() {
  const { projectId } = useParams();

  const { data: project = {} } = useGetProjectDetailsQuery(projectId, {
    skip: !projectId,
  });

  return project.canManage ? (
    <MetadataTemplateManager />
  ) : (
    <MetadataTemplateMember />
  );
}

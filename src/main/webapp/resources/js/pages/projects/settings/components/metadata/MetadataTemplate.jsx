import React from "react";
import { useParams } from "react-router-dom";
import { useGetProjectDetailsQuery } from "../../../../../apis/projects/project";
import MetadataTemplateManager from "./MetadataTemplateManager";
import MetadataTemplateMember from "./MetadataTemplateMember";

export default function MetadataTemplate() {
  const { id, projectId } = useParams();

  const { data: project = {} } = useGetProjectDetailsQuery(projectId, {
    skip: !projectId,
  });

  return project.canManage ? (
    <MetadataTemplateManager />
  ) : (
    <MetadataTemplateMember />
  );
}

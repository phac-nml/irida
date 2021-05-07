import { Button, Typography } from "antd";
import React from "react";
import { deleteProject } from "../../../../apis/projects/project";

export default function DeleteProject({ projectId }) {
  return (
    <>
      <Typography.Title level={2}>
        {i18n("project.settings.page.delete")}
        <Button danger onClick={() => deleteProject(projectId)}>
          DELETE ME
        </Button>
      </Typography.Title>
    </>
  );
}

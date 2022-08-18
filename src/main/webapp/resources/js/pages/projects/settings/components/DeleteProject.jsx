import { Alert, Button, Checkbox, notification, Space, Typography } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import { deleteProject } from "../../../../apis/projects/project";

/**
 * React component to delete a project
 * @param {number} projectId - identifier for the current project
 * @returns {JSX.Element}
 * @constructor
 */
export default function DeleteProject() {
  const { projectId } = useParams();
  const [disabled, setDisabled] = React.useState(true);
  const [loading, setLoading] = React.useState(false);

  const doDeleteProject = () => {
    setLoading(true);
    deleteProject(projectId).catch((message) => {
      setLoading(false);
      notification.error({ message });
    });
  };

  return (
    <>
      <Typography.Title level={2}>
        {i18n("DeleteProject.title")}
      </Typography.Title>

      <Space direction="vertical" size={"middle"}>
        <Alert
          showIcon
          type={"warning"}
          message={i18n("DeleteProject.warning")}
          description={
            <ul>
              <li>{i18n("DeleteProject.consequences.samples")}</li>
              <li>{i18n("DeleteProject.consequences.copied")}</li>
              <li>{i18n("DeleteProject.consequences.reference")}</li>
              <li>{i18n("DeleteProject.consequences.associated")}</li>
              <li>{i18n("DeleteProject.consequences.analyses")}</li>
              <li>{i18n("DeleteProject.consequences.ncbi")}</li>
            </ul>
          }
        />

        <Checkbox
          className="t-confirm-delete-project"
          onChange={(e) => setDisabled(!e.target.checked)}
        >
          {i18n("DeleteProject.confirm")}
        </Checkbox>
        <Button
          className="t-delete-project-button"
          type="primary"
          danger
          disabled={disabled}
          onClick={doDeleteProject}
          loading={loading}
        >
          {i18n("DeleteProject.submit")}
        </Button>
      </Space>
    </>
  );
}

import React, { useEffect, useState } from "react";
import { Avatar, List } from "antd";
import { ConfirmButton } from "../../../components/Buttons";
import { createProjectLink } from "../../../utilities/link-utilities";
import {
  getAssociatedProjects,
  removeAssociatedProject
} from "../../../apis/projects/projects";

export function ViewAssociatedProjects() {
  const [projects, setProjects] = useState();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAssociatedProjects(window.project.id).then(data => {
      setProjects(data.associatedProjectList);
      setLoading(false);
    });
  }, [getAssociatedProjects]);

  function removeAssociated(id) {
    removeAssociatedProject(window.project.id, id).then(() =>
      setProjects(projects.filter(p => p.id !== id))
    );
  }
  return (
    <List
      loading={loading}
      bordered
      itemLayout="horizontal"
      dataSource={projects}
      renderItem={project => (
        <List.Item
          actions={[
            <ConfirmButton
              title="Confirm removal"
              key="remove"
              onConfirm={() => removeAssociated(project.id)}
              label="remove"
            />
          ]}
        >
          <List.Item.Meta
            avatar={<Avatar icon="folder" size="large" />}
            title={createProjectLink(project)}
            description={project.organism || "unknown"}
          />
        </List.Item>
      )}
    />
  );
}

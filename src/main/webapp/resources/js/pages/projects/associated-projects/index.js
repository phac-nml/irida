import React, { useEffect, useState } from "react";
import { render } from "react-dom";
import {
  getAssociatedProjects,
  removeAssociatedProject
} from "../../../apis/projects/projects";
import { Avatar, Button, List, Popconfirm, Typography } from "antd";

const { Title } = Typography;

function AssociatedProjects() {
  const [projects, setProjects] = useState();

  useEffect(() => {
    getAssociatedProjects(window.project.id).then(r =>
      setProjects(r.associatedProjectList)
    );
  }, [getAssociatedProjects]);

  function removeAssociated(id) {
    removeAssociatedProject(window.project.id, id).then(() =>
      setProjects(projects.filter(p => p.id !== id))
    );
  }

  return (
    <section>
      <Title level={3}>Associated Projects</Title>
      <List
        bordered
        itemLayout="horizontal"
        dataSource={projects}
        renderItem={project => (
          <List.Item
            actions={[
              <Popconfirm
                title="Confirm removal"
                key="remove"
                onConfirm={() => removeAssociated(project.id)}
              >
                <Button type="link">remove</Button>
              </Popconfirm>
            ]}
          >
            <List.Item.Meta
              avatar={<Avatar icon="folder" />}
              title={
                <a href={`${window.TL.BASE_URL}projects/${project.id}`}>
                  {project.label}
                </a>
              }
              description={project.organism || "unknown"}
            />
          </List.Item>
        )}
      />
    </section>
  );
}

render(<AssociatedProjects />, document.querySelector("#root"));

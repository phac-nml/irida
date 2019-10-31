import React, { useEffect, useState } from "react";
import { Avatar, Input, Switch, Table, Typography } from "antd";
import uniqBy from "lodash/uniqBy";
import { createProjectLink } from "../../../utilities/link-utilities";
import {
  addAssociatedProject,
  getAssociatedProjects,
  removeAssociatedProject
} from "../../../apis/projects/projects";

const { Text } = Typography;

export function ViewAssociatedProjects() {
  const [projects, setProjects] = useState([]);
  const [organismFilters, setOrganismFilters] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAssociatedProjects(window.project.id).then(data => {
      setProjects(data.associatedProjectList);
      const organisms = uniqBy(
        data.associatedProjectList.filter(p => p.organism),
        "organism"
      ).map(item => ({
        text: item.organism,
        value: item.organism
      }));
      organisms.push({ text: "Unknown", value: "unknown" });
      setOrganismFilters(organisms);
      setLoading(false);
    });
  }, [getAssociatedProjects]);

  function updateProject(checked, project) {
    setLoading(true);
    let promise;
    if (checked) {
      promise = addAssociatedProject(window.project.id, project.id);
    } else {
      promise = removeAssociatedProject(window.project.id, project.id);
    }
    promise.then(() => {
      project.associated = checked;
      setProjects([...projects]);
      setLoading(false);
    });
  }

  const columns = [
    {
      width: 50,
      render: project =>
        window.PAGE.permissions ? (
          <Switch
            checked={project.associated}
            loading={project.updating}
            onClick={checked => updateProject(checked, project)}
          />
        ) : (
          <Avatar icon="folder" />
        )
    },
    {
      render: project => createProjectLink(project),
      title: "Project",
      filterDropdown: ({
        setSelectedKeys,
        selectedKeys,
        confirm,
        clearFilters
      }) => {
        return (
          <div style={{ padding: 8 }}>
            <Input
              style={{ width: 188, display: "block" }}
              value={selectedKeys[0]}
              onChange={e =>
                setSelectedKeys(e.target.value ? [e.target.value] : [])
              }
              onPressEnter={confirm}
            />
          </div>
        );
      },
      onFilter: (value, project) => {
        console.log("KDFSLJLSDJF");
        return project.label
          .toString()
          .toLocaleLowerCase()
          .includes(value.toLowerCase());
      }
    },
    {
      dataIndex: "organism",
      align: "right",
      title: "Organism",
      render: text => <Text type="secondary">{text}</Text>,
      filters: organismFilters,
      onFilter: (value, record) =>
        record.organism === value || (!record.organism && value === "unknown")
    }
  ];

  return (
    <Table
      rowKey="id"
      loading={loading}
      columns={columns}
      dataSource={projects}
    />
  );
}

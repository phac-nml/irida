import React, { useEffect, useState } from "react";
import { Avatar, Button, Input, Switch, Table, Typography } from "antd";
import { createProjectLink } from "../../../utilities/link-utilities";
import {
  addAssociatedProject,
  getAssociatedProjects,
  removeAssociatedProject
} from "../../../apis/projects/projects";
import { createListFilterByUniqueAttribute } from "../../../components/Tables/filter-utilities";
import { grey3 } from "../../../styles/colors";

const { Text } = Typography;

export function ViewAssociatedProjects() {
  const [projects, setProjects] = useState([]);
  const [organismFilters, setOrganismFilters] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAssociatedProjects(window.project.id).then(data => {
      setProjects(data.associatedProjectList);
      setOrganismFilters(
        createListFilterByUniqueAttribute({
          list: data.associatedProjectList,
          attr: "organism"
        })
      );
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
      key: "toggle",
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
      key: "project",
      render: project => createProjectLink(project),
      title: "Project",
      filterDropdown: ({
        setSelectedKeys,
        selectedKeys,
        confirm,
        clearFilters
      }) => {
        return (
          <div>
            <div style={{ padding: 8, borderBottom: `1px solid ${grey3}` }}>
              <Input
                style={{ width: 188, display: "block" }}
                value={selectedKeys[0]}
                onChange={e =>
                  setSelectedKeys(e.target.value ? [e.target.value] : [])
                }
                onPressEnter={confirm}
              />
            </div>
            <div style={{ padding: 8 }}>
              <Button
                onClick={clearFilters}
                size="small"
                style={{ width: 90, marginRight: 8 }}
              >
                Reset
              </Button>
              <Button
                type="primary"
                onClick={confirm}
                icon="search"
                size="small"
                style={{ width: 90 }}
              >
                Search
              </Button>
            </div>
          </div>
        );
      },
      onFilter: (value, project) => {
        console.log("KDFSLJLSDJF", value, project);
        return project.label
          .toString()
          .toLocaleLowerCase()
          .includes(value.toLowerCase());
      }
    },
    {
      key: "organism",
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

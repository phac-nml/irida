import React, { useEffect, useState } from "react";
import { Avatar, Switch, Table, Typography } from "antd";
import { createProjectLink } from "../../../utilities/link-utilities";
import {
  addAssociatedProject,
  getAssociatedProjects,
  removeAssociatedProject
} from "../../../apis/projects/projects";
import { TextFilter } from "../../../components/Tables/fitlers";
import { createListFilterByUniqueAttribute } from "../../../components/Tables/filter-utilities";
import { SPACE_MD } from "../../../styles/spacing";

const { Text } = Typography;

export default function ViewAssociatedProjects() {
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
      key: "project",
      render: project => (
        <>
          <span style={{ marginRight: SPACE_MD }}>
            {window.PAGE.permissions ? (
              <Switch
                checked={project.associated}
                loading={project.updating}
                onClick={checked => updateProject(checked, project)}
              />
            ) : (
              <Avatar icon="folder" />
            )}
          </span>{" "}
          {createProjectLink(project)}
        </>
      ),
      title: "Project",
      filterDropdown: props => <TextFilter {...props} />,
      onFilter: (value, project) => {
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
      bordered
      rowKey="id"
      loading={loading}
      columns={columns}
      dataSource={projects}
    />
  );
}

/**
 * @File Component to display a Table of associated projects.  If the user is a project
 * manager or admin, they will be shown all their available projects, with the
 * ability to add or remove them as associated projects.
 */
import React, { useEffect, useState } from "react";
import { Avatar, Switch, Table, Typography } from "antd";
import { createProjectLink } from "../../../utilities/link-utilities";
import {
  addAssociatedProject,
  getAssociatedProjects,
  removeAssociatedProject
} from "../../../apis/projects/associated-projects";
import { TextFilter } from "../../../components/Tables/fitlers";
import { createListFilterByUniqueAttribute } from "../../../components/Tables/filter-utilities";
import { getI18N } from "../../../utilities/i18n-utilities";

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
    window.PAGE.permissions
      ? {
          key: "switch",
          width: 80,
          defaultSortOrder: "descend",
          sorter: (a, b) => (a.associated ? 1 : b.associated ? -1 : 1),
          render(project) {
            return (
              <Switch
                className="t-selection"
                checked={project.associated}
                loading={project.updating}
                onClick={checked => updateProject(checked, project)}
              />
            );
          }
        }
      : {
          key: "icon",
          width: 60,
          render() {
            return <Avatar icon="folder" />;
          }
        },
    {
      key: "project",
      render(project) {
        return createProjectLink(project);
      },
      title: getI18N("ViewAssociatedProjects.ProjectHeader"),
      filterDropdown(props) {
        return <TextFilter {...props} />;
      },
      onFilter: (value, project) => {
        return project.label
          .toString()
          .toLowerCase()
          .includes(value.toLowerCase());
      },
      sorter: (a, b) => ("" + a.label).localeCompare("" + b.label)
    },
    {
      key: "organism",
      dataIndex: "organism",
      align: "right",
      title: "Organism",
      render(text) {
        return <Text type="secondary">{text}</Text>;
      },
      filters: organismFilters,
      onFilter: (value, record) =>
        record.organism === value || (!record.organism && value === "unknown"),
      sorter: (a, b) => ("" + a.organism).localeCompare("" + b.organism)
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

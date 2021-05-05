/**
 * @File Component to display a Table of associated projects.  If the user is a project
 * manager or admin, they will be shown all their available projects, with the
 * ability to add or remove them as associated projects.
 */
import { Avatar, Switch, Table, Typography } from "antd";
import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import {
  useAddAssociatedProjectMutation,
  useGetAssociatedProjectsQuery,
  useRemoveAssociatedProjectMutation,
} from "../../../../../apis/projects/associated-projects";
import { IconFolder } from "../../../../../components/icons/Icons";
import { createListFilterByUniqueAttribute } from "../../../../../components/Tables/filter-utilities";
import { TextFilter } from "../../../../../components/Tables/fitlers";
import { setBaseUrl } from "../../../../../utilities/url-utilities";

const { Text } = Typography;

export default function ViewAssociatedProjects() {
  const [organismFilters, setOrganismFilters] = useState([]);
  const { canManage, id: projectId } = useSelector((state) => state.project);
  const [switches, setSwitches] = React.useState({});

  const { data: projects, isLoading } = useGetAssociatedProjectsQuery(
    projectId
  );
  const [addAssociatedProject] = useAddAssociatedProjectMutation();
  const [removeAssociatedProject] = useRemoveAssociatedProjectMutation();

  useEffect(() => {
    if (projects?.length) {
      console.log(projects);
      setOrganismFilters(
        createListFilterByUniqueAttribute({
          list: projects,
          attr: "organism",
        })
      );
    }
  }, [projects]);

  function updateProject(checked, project) {
    const updateFn = checked ? addAssociatedProject : removeAssociatedProject;
    setSwitches({ ...switches, [project.id]: true });
    updateFn({
      projectId,
      associatedProjectId: project.id,
    }).then(() => setSwitches({ ...switches, [project.id]: false }));
  }

  const columns = [
    canManage
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
                loading={switches[project.id]}
                onClick={(checked) => updateProject(checked, project)}
              />
            );
          },
        }
      : {
          key: "icon",
          width: 60,
          render() {
            return <Avatar icon={<IconFolder />} />;
          },
        },
    {
      key: "project",
      render(project) {
        return (
          <a href={setBaseUrl(`projects/${project.id}`)}>{project.label}</a>
        );
      },
      title: i18n("ViewAssociatedProjects.ProjectHeader"),
      filterDropdown(props) {
        return <TextFilter {...props} />;
      },
      onFilter: (value, project) => {
        return project.label
          .toString()
          .toLowerCase()
          .includes(value.toLowerCase());
      },
      sorter: (a, b) => ("" + a.label).localeCompare("" + b.label),
    },
    {
      key: "organism",
      dataIndex: "organism",
      align: "right",
      title: i18n("ViewAssociatedProjects.OrganismHeader"),
      render(text) {
        return <Text type="secondary">{text}</Text>;
      },
      filters: organismFilters,
      onFilter: (value, record) =>
        record.organism === value || (!record.organism && value === "unknown"),
      sorter: (a, b) => ("" + a.organism).localeCompare("" + b.organism),
    },
  ];

  return (
    <Table
      bordered
      rowKey="id"
      loading={isLoading}
      columns={columns}
      dataSource={projects}
    />
  );
}

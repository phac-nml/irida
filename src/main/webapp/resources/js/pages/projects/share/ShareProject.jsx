import { Select, Space, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  useGetPotentialProjectsToShareToQuery
} from "../../../apis/projects/projects";
import { setProject } from "./shareSlice";

/**
 * React component for selecting the project to share a sample with.
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProject() {
  const dispatch = useDispatch();
  const { currentProject } = useSelector((state) => state.shareReducer);
  const [options, setOptions] = React.useState([]);

  /*
  This fetches a list of the projects that the user has access to.
   */
  const {
    data: projects = [],
    isLoading: projectLoading,
  } = useGetPotentialProjectsToShareToQuery(currentProject, {
    skip: !currentProject,
  });

  const formatOptions = (values) => {
    if (!values) return [];
    return values.map((project) => ({
      label: project.name,
      value: project.identifier,
    }));
  };

  React.useEffect(() => {
    if (!projectLoading) {
      setOptions(formatOptions(projects));
    }
  }, [projects, projectLoading]);
  React.useEffect(() => {
    setOptions(
      projects.map((project) => ({
        label: project.name,
        value: project.identifier,
      }))
    );
  }, [projects]);

  const handleSearch = (value) => {
    const lowerValue = value.toLowerCase();
    const available = projects.filter((project) =>
      project.name.toLowerCase().includes(lowerValue)
    );
    console.log(available);
    const formatted = formatOptions(available);
    console.log(formatted);
    setOptions(formatted);
  };

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Typography.Text strong>{i18n("ShareSamples.projects")}</Typography.Text>
      <Select
        autoFocus
        showSearch
        size="large"
        style={{ width: `100%` }}
        loading={projectLoading}
        options={options}
        className="t-share-project"
        filterOption={false}
        onSearch={handleSearch}
        onChange={(projectId) => dispatch(setProject(projectId))}
      />
    </Space>
  );
}

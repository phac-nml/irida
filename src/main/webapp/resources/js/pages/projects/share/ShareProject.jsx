import { Select, Space, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { setProject } from "./shareSlice";

/**
 * React component for selecting the project to share a sample with.
 * @param {list} projects - list of projects that the user is a manager on
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProject({ projects }) {
  const dispatch = useDispatch();
  const { targetProject } = useSelector((state) => state.shareReducer);
  const [options, setOptions] = React.useState(() => formatOptions(projects));

  function formatOptions(values) {
    if (!values) return [];
    return values.map((project) => ({
      label: project.name,
      value: project.identifier,
    }));
  }

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
    const formatted = formatOptions(available);
    setOptions(formatted);
  };

  function onChange(projectId) {
    const project = projects.find((p) => p.identifier === projectId);
    dispatch(setProject(project));
  }

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Typography.Title level={5}>
        {i18n("ShareSamples.projects")}
      </Typography.Title>
      <Select
        autoFocus
        showSearch
        size="large"
        style={{ width: `100%` }}
        options={options}
        className="t-share-project"
        filterOption={false}
        onSearch={handleSearch}
        onChange={onChange}
        defaultValue={targetProject ? targetProject.identifier : null}
      />
    </Space>
  );
}

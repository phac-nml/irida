import { Select, Space, Tag, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { setProject } from "./shareSlice";

const { Text } = Typography;

/**
 * React component for selecting the project to share a sample with.
 * @param {list} projects - list of projects that the user is a manager on
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProject({ projects }) {
  const dispatch = useDispatch();
  const { targetProject } = useSelector((state) => state.shareReducer);
  const [selectList, setSelectList] = React.useState(() => projects);

  const handleSearch = (value) => {
    const lowerValue = value.toLowerCase();
    const filteredProjects = projects.filter((project) =>
      project.name.toLowerCase().includes(lowerValue)
    );
    setSelectList(filteredProjects);
  };

  function onChange(projectId) {
    const project = projects.find((p) => p.identifier === projectId);
    dispatch(setProject(project));
  }

  const options = selectList.map((project) => (
    <Select.Option key={project.identifier} value={project.identifier}>
      <>
        <Text>{project.name}</Text>
        <Tag style={{ float: "right" }}>{project.identifier}</Tag>
      </>
    </Select.Option>
  ));

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
        className="t-share-project"
        filterOption={false}
        onSearch={handleSearch}
        onChange={onChange}
        defaultValue={targetProject ? targetProject.identifier : null}
      >
        {options}
      </Select>
    </Space>
  );
}

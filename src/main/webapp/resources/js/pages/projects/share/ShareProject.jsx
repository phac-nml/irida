import { Space, Typography } from "antd";
import React from "react";
import { SearchByNameAndIdSelect } from "../../../components/selects/SearchByNameAndIdSelect";
import { setProject } from "./shareSlice";
import { useDispatch, useSelector } from "react-redux";

/**
 * React component for selecting the project to share a sample with.
 * @param {list} projects - list of projects that the user is a manager on
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProject({ projects }) {
  const dispatch = useDispatch();
  const { targetProject } = useSelector((state) => state.shareReducer);

  function onChange(projectId) {
    const project = projects.find((p) => p.identifier === projectId);
    dispatch(setProject(project));
  }

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Typography.Title level={5}>
        {i18n("ShareSamples.projects")}
      </Typography.Title>
      <SearchByNameAndIdSelect
        selectList={projects.map((project) => ({
          id: project.identifier,
          name: project.name,
        }))}
        onChange={onChange}
        defaultValue={targetProject?.identifier}
        className="t-project-select"
      />
    </Space>
  );
}

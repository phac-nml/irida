import { Space, Typography } from "antd";
import React from "react";
import { ProjectSelect } from "../../../components/project/ProjectSelect";
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
    const project = projects.find((p) => p.id === projectId);
    dispatch(setProject(project));
  }

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Typography.Title level={5}>
        {i18n("ShareSamples.projects")}
      </Typography.Title>
      <ProjectSelect
        projects={projects}
        onChange={onChange}
        defaultValue={targetProject?.id}
      />
    </Space>
  );
}

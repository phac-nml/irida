import { Space, Typography } from "antd";
import React from "react";
import { SearchByNameAndIdSelect } from "../../../components/selects/SearchByNameAndIdSelect";
import { setProject } from "./shareSlice";
import { useDispatch, useSelector } from "react-redux";
import { Project } from "../../../types/irida";
import { ShareState } from "./store";

interface ShareProjectProps {
  projects: Project[];
}

/**
 * React component for selecting the project to share a sample with.
 */
export function ShareProject({ projects }: ShareProjectProps): JSX.Element {
  const dispatch = useDispatch();
  const { targetProject } = useSelector(
    (state: ShareState) => state.shareReducer
  );

  function onChange(projectId: number) {
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
      />
    </Space>
  );
}

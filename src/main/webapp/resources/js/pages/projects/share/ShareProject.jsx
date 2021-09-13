import { Form, Select, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useGetPotentialProjectsToShareToQuery } from "../../../apis/projects/projects";
import { setProject } from "./shareSlice";

/**
 * React component for selecting the project to share a sample with.
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProject() {
  const dispatch = useDispatch();
  const { currentProject } = useSelector((state) => state.shareReducer);
  const [options, setOptions] = React.useState();

  /*
  This fetches a list of the projects that the user has access to.
   */
  const {
    data: projects,
    isLoading: projectLoading,
  } = useGetPotentialProjectsToShareToQuery(currentProject, {
    skip: !currentProject,
  });

  React.useEffect(() => {
    if (!projectLoading) {
      setOptions(
        projects.map((project) => ({
          label: project.name,
          value: project.identifier,
        }))
      );
    }
  }, [projects, projectLoading]);

  return (
    <Form.Item
      label={
        <Typography.Text strong>
          {i18n("ShareSamples.projects")}
        </Typography.Text>
      }
    >
      <Select
        autoFocus
        size="large"
        style={{ width: `100%` }}
        loading={projectLoading}
        options={options}
        onChange={(projectId) => dispatch(setProject(projectId))}
      />
    </Form.Item>
  );
}

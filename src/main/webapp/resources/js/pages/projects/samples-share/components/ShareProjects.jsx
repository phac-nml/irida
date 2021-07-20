import { Button, Select, Space, Tag, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useGetProjectsManagedByUserQuery } from "../../../../apis/projects/projects";
import { IconArrowRight } from "../../../../components/icons/Icons";
import { setTargetProject, setNextStep } from "../services/shareSlice";

/**
 * React component to select the destination project.
 * @param {number} projectId - identifier for the current project
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProjects({ projectId }) {
  const dispatch = useDispatch();
  const { data: projects } = useGetProjectsManagedByUserQuery(projectId);
  const { destination } = useSelector((state) => state.reducer);
  const [options, setOptions] = React.useState([]);

  /**
   * When the value of the select changes store the new project information
   * in redux.
   * @param {number} index - Index of the selected project
   * @returns {*}
   */
  const onChange = (index) => dispatch(setTargetProject(projects[index]));

  React.useEffect(() => {
    if (projects) {
      setOptions(
        projects.map((project, index) => (
          <Select.Option key={`project-${project.identifier}`} value={index}>
            <Space>
              {project.label}
              {project.organism && <Tag>{project.organism}</Tag>}
            </Space>
          </Select.Option>
        ))
      );
    }
  }, [projects]);

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Typography.Text>
        Search for a project to copy the samples to:
      </Typography.Text>
      <Select
        autoFocus={true}
        showSearch
        size="large"
        style={{ width: `100%` }}
        onChange={onChange}
        value={destination?.name}
        filterOption={(input, option) => {
          return option.children.props.children[0]
            .toLowerCase()
            .includes(input.toLowerCase());
        }}
      >
        {options}
      </Select>

      <div style={{ display: "flex", flexDirection: "row-reverse" }}>
        <Button onClick={() => dispatch(setNextStep())} disabled={!destination}>
          <Space>
            <span>Review Samples</span>
            <IconArrowRight />
          </Space>
        </Button>
      </div>
    </Space>
  );
}

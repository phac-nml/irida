import { navigate } from "@reach/router";
import { Button, Select, Space, Tag, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useGetProjectsManagedByUserQuery } from "../../../../apis/projects/projects";
import { IconArrowRight } from "../../../../components/icons/Icons";
import { setDestinationProject } from "../services/rootReducer";

/**
 * React component to select the destination project.
 * @param {number} projectId - identifier for the current project
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProjects({ projectId }) {
  const dispatch = useDispatch();
  const { data: projects } = useGetProjectsManagedByUserQuery(projectId);
  const { destinationId } = useSelector((state) => state.reducer);
  const [options, setOptions] = React.useState([]);

  const onChange = (value) => dispatch(setDestinationProject(value));

  React.useEffect(() => {
    if (projects) {
      setOptions(
        projects.map((project) => (
          <Select.Option
            key={`project-${project.identifier}`}
            value={project.identifier}
          >
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
        value={destinationId}
        optionFilterProp="children"
        filterOption={(input, option) => {
          return option.children.props.children[0]
            .toLowerCase()
            .includes(input.toLowerCase());
        }}
      >
        {options}
      </Select>

      <div style={{ display: "flex", flexDirection: "row-reverse" }}>
        <Button onClick={() => navigate("samples")} disabled={!destinationId}>
          <Space>
            <span>Review Samples</span>
            <IconArrowRight />
          </Space>
        </Button>
      </div>
    </Space>
  );
}

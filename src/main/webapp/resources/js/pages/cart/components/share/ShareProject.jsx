import { Button, Select, Space, Spin } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useGetProjectsForUserQuery } from "../../../../apis/projects/projects";
import { nextStep, setProject } from "./shareSlice";

/**
 * React component to render a select field for selection of the project
 * to share samples with.
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProject() {
  const project = useSelector((state) => state.share.project);
  const [query, setQuery] = React.useState("");
  const dispatch = useDispatch();

  const { data: projects = [], isFetching } = useGetProjectsForUserQuery(query);

  const setValue = (newValue) => {
    const project = projects.find((project) => project.identifier === newValue);
    console.log(project);
    dispatch(setProject(project));
  };

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Select
        size="large"
        value={project?.identifier}
        onChange={setValue}
        options={projects.map((project) => ({
          label: project.name,
          value: project.identifier,
        }))}
        showSearch
        onSearch={setQuery}
        style={{ width: `100%` }}
        filterOption={false}
        notFoundContent={isFetching ? <Spin size="small" /> : null}
      />
      <div style={{ display: "flex", flexDirection: "row-reverse" }}>
        <Button disabled={!project} onClick={() => dispatch(nextStep())}>
          NEXT
        </Button>
      </div>
    </Space>
  );
}

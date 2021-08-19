import { Form, Select, Space, Table, Tag } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useSelector } from "react-redux";
import { useGetProjectsToShareToQuery } from "../../../apis/projects/projects";
import { useGetSampleIdsForProjectQuery } from "../../../apis/projects/samples";
import store from "./store";

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareSamples() {
  const [samples, setSamples] = React.useState([]);
  const [projectId, setProjectId] = React.useState();
  const { samples: originalSamples, currentProject } = useSelector(
    (state) => state.shareReducer
  );

  const {
    data: projects,
    isLoading: projectLoading,
  } = useGetProjectsToShareToQuery(currentProject, {
    skip: !currentProject,
  });

  const { data: sampleIds, isLoading } = useGetSampleIdsForProjectQuery(
    projectId,
    {
      skip: !projectId,
    }
  );

  React.useEffect(() => {
    setSamples(originalSamples);
  }, [originalSamples]);

  React.useEffect(() => {
    if (sampleIds) {
      setSamples(
        originalSamples.map((sample) => ({
          ...sample,
          exists: sampleIds.includes(sample.id),
        }))
      );
    }
  }, [originalSamples, sampleIds]);

  const updateCurrentSampleIds = (projectId) => {
    setProjectId(projectId);
  };

  return (
    <Form layout="vertical">
      <Space direction="vertical" style={{ display: "block" }} size="large">
        <Form.Item label={"Select a project to share the samples with"}>
          <Select
            style={{ width: `100%` }}
            loading={projectLoading}
            options={projects?.map((project) => ({
              label: project.name,
              value: project.identifier,
            }))}
            onChange={updateCurrentSampleIds}
          />
        </Form.Item>
        <Table
          loading={!samples}
          dataSource={samples}
          rowKey={(sample) => `sample-${sample.id}`}
          columns={[
            {
              title: "Name",
              dataIndex: "name",
            },
            {
              title: "",
              dataIndex: "exists",
              render: (text, sample) =>
                sample.exists && <Tag color="red">EXISTS</Tag>,
            },
          ]}
        />
      </Space>
    </Form>
  );
}

render(
  <Provider store={store}>
    <ShareSamples />
  </Provider>,
  document.querySelector("#root")
);

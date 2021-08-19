import { Select, Table } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
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
  const [samples, setSamples] = React.useState();
  const [projectId, setProjectId] = React.useState();
  const [currentId, setCurrentId] = React.useState();
  const {
    data: projects,
    isLoading: projectLoading,
  } = useGetProjectsToShareToQuery(currentId, {
    skip: !currentId,
  });
  const { data: sampleIds, isLoading } = useGetSampleIdsForProjectQuery(
    projectId,
    {
      skip: !projectId,
    }
  );

  React.useEffect(() => {
    const stringData = window.sessionStorage.getItem("share");
    const data = JSON.parse(stringData);

    setSamples(data.samples);
    setCurrentId(data.projectId);
  }, []);

  const updateCurrentSampleIds = (projectId) => {
    setProjectId(projectId);
  };

  return (
    <>
      <Select
        style={{ width: `100%` }}
        loading={projectLoading}
        options={projects?.map((project) => ({
          label: project.name,
          value: project.identifier,
        }))}
        onChange={updateCurrentSampleIds}
      />
      <Table
        loading={!samples}
        dataSource={samples}
        rowKey={(sample) => `sample-${sample.id}`}
        columns={[{ title: "Name", dataIndex: "name" }]}
      />
    </>
  );
}

render(
  <Provider store={store}>
    <ShareSamples />
  </Provider>,
  document.querySelector("#root")
);

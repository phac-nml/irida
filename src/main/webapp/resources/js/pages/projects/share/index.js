import { Form, Select, Space } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useDispatch, useSelector } from "react-redux";
import { useGetProjectsToShareToQuery } from "../../../apis/projects/projects";
import { useGetSampleIdsForProjectQuery } from "../../../apis/projects/samples";
import { ShareSamples } from "./ShareSamples";
import { setProject } from "./shareSlice";
import store from "./store";

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareLayout() {
  const dispatch = useDispatch();
  const { originalSamples, currentProject, projectId } = useSelector(
    (state) => state.shareReducer
  );

  const {
    data: projects,
    isLoading: projectLoading,
  } = useGetProjectsToShareToQuery(currentProject, {
    skip: !currentProject,
  });

  const { data: sampleIds } = useGetSampleIdsForProjectQuery(projectId, {
    skip: !projectId,
  });

  const updateCurrentSampleIds = (projectId) => dispatch(setProject(projectId));

  return (
    <Form layout="vertical">
      <Space direction="vertical" style={{ display: "block" }} size="large">
        <Form.Item label={i18n("ShareSamples.projects")}>
          <Select
            size="large"
            style={{ width: `100%` }}
            loading={projectLoading}
            options={projects?.map((project) => ({
              label: project.name,
              value: project.identifier,
            }))}
            onChange={updateCurrentSampleIds}
          />
        </Form.Item>
        {sampleIds && <ShareSamples sampleIds={sampleIds} />}
      </Space>
    </Form>
  );
}

render(
  <Provider store={store}>
    <ShareLayout />
  </Provider>,
  document.querySelector("#root")
);

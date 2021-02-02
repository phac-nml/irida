import React from "react";
import { render } from "react-dom";
import { Router, useMatch } from "@reach/router";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Button, Divider, List, Space, Typography } from "antd";
import { ProcessingCoverage } from "./ProcessingCoverage";
import { ProcessingPriorities } from "./ProcessingPriorities";
import { fetchAutomatedIridaAnalysisWorkflows } from "../../../apis/pipelines/pipelines";
import { useVisibility } from "../../../contexts/visibility-context";

const ProcessingLayout = ({ children }) => (
  <div>
    <Typography.Title level={2}>
      {i18n("project.settings.automated-pipelines")}
    </Typography.Title>
    {children}
  </div>
);

const Info = () => {
  const [addPipelineVisible] = useVisibility();
  const match = useMatch("/projects/:projectId/settings/processing");
  const [pipelines, setPipelines] = React.useState([]);

  React.useEffect(() => {
    fetchAutomatedIridaAnalysisWorkflows().then(setPipelines);
  }, []);

  React.useEffect(() => {
    // TODO: Come back here please
  }, [addPipelineVisible]);

  return (
    <Space style={{ width: `100%` }} direction="vertical">
      <ProcessingPriorities projectId={match.projectId} />
      <Divider />
      <ProcessingCoverage projectId={match.projectId} />
      <Divider />
      <List
        bordered
        dataSource={pipelines}
        renderItem={(item) => (
          <List.Item key={item.id}>
            <List.Item.Meta title={item.name} description={item.description} />
            <Button>Add Pipelien</Button>
          </List.Item>
        )}
      />
    </Space>
  );
};

const Pipelines = () => <div>Pipelines</div>;

render(
  <Router>
    <ProcessingLayout path={setBaseUrl(`/projects/1/settings/processing`)}>
      <Info path={`/`} />
      <Pipelines path={`pipelines`} />
    </ProcessingLayout>
  </Router>,
  document.querySelector("#process-root")
);

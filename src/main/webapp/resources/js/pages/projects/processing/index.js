import React from "react";
import { render } from "react-dom";
import { Router, useMatch } from "@reach/router";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Space, Typography } from "antd";
import { ProcessingCoverage } from "./ProcessingCoverage";
import { ProcessingPriorities } from "./ProcessingPriorities";

const ProcessingLayout = ({ children }) => (
  <div>
    <Typography.Title level={2}>
      {i18n("project.settings.automated-pipelines")}
    </Typography.Title>
    {children}
  </div>
);

const Info = () => {
  const match = useMatch("/projects/:projectId/settings/processing");

  return (
    <Space style={{ width: `100%` }} direction="vertical">
      <ProcessingPriorities projectId={match.projectId} />
      <ProcessingCoverage projectId={match.projectId} />
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

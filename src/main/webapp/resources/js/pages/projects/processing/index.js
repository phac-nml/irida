import React from "react";
import { render } from "react-dom";
import { Router, useMatch } from "@reach/router";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Divider, Space, Typography } from "antd";
import { ProcessingCoverage } from "./ProcessingCoverage";
import { ProcessingPriorities } from "./ProcessingPriorities";
import {
  useVisibility,
  VisibilityProvider,
} from "../../../contexts/visibility-context";
import { AddPipelineModal } from "./AddPipelineModal";

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

  React.useEffect(() => {
    console.log("VISIBILITY CHANGED");
    // TODO: Come back here please
  }, [addPipelineVisible]);

  return (
    <Space style={{ width: `100%` }} direction="vertical">
      <ProcessingPriorities projectId={match.projectId} />
      <Divider />
      <ProcessingCoverage projectId={match.projectId} />
      <Divider />
      <AddPipelineModal projectId={match.projectId} />
    </Space>
  );
};

const Pipelines = () => <div>Pipelines</div>;

render(
  <VisibilityProvider>
    <Router>
      <ProcessingLayout path={setBaseUrl(`/projects/1/settings/processing`)}>
        <Info path={`/`} />
        <Pipelines path={`pipelines`} />
      </ProcessingLayout>
    </Router>
  </VisibilityProvider>,
  document.querySelector("#process-root")
);

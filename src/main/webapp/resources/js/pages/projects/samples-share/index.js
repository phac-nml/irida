import { Router } from "@reach/router";
import { Col, Grid, Row, Steps } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useSelector } from "react-redux";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareFinish } from "./components/ShareFinish";
import { ShareMetadataFields } from "./components/ShareMetadataFields";
import { ShareProjects } from "./components/ShareProjects";
import { ShareSamplesList } from "./components/ShareSamplesList";
import store from "./store";

const { useBreakpoint } = Grid;

function ShareSamples({ projectId }) {
  const screens = useBreakpoint();
  const { samples, projectId: sharedProjectId, step } = useSelector(
    (state) => state.reducer
  );

  React.useEffect(() => {
    if (
      !sharedProjectId ||
      Number(sharedProjectId) !== Number(projectId) ||
      samples === undefined
    ) {
      sessionStorage.removeItem("share");
      window.location.href = setBaseUrl(`/projects/${projectId}`);
    }
  }, [projectId, samples, sharedProjectId]);

  const steps = [
    <ShareProjects key="projects" projectId={projectId} />,
    <ShareSamplesList key="samples" projectId={projectId} />,
    <ShareMetadataFields key="fields" projectId={projectId} />,
    <ShareFinish key="finish" />,
  ];

  return (
    <Row gutter={[16, 16]}>
      <Col xs={24}>
        <Steps
          direction={screens.md ? "horizontal" : "vertical"}
          current={step}
        >
          <Steps.Step title={"DESTINATION PROJECT"} />
          <Steps.Step title={"REVIEW SAMPLES"} />
          <Steps.Step title={"CHECK METADATA FIELD RESTRICTIONS"} />
        </Steps>
      </Col>
      <Col xs={24}>{steps[step]}</Col>
    </Row>
  );
}

render(
  <Provider store={store}>
    <Router>
      <ShareSamples path={setBaseUrl(`/projects/:projectId/samples-share`)} />
    </Router>
  </Provider>,
  document.querySelector("#root")
);

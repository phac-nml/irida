import { Router } from "@reach/router";
import { Col, Grid, Row, Steps } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useDispatch, useSelector } from "react-redux";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareFinish } from "./components/ShareFinish";
import { ShareMetadataFields } from "./components/ShareMetadataFields";
import { ShareProjects } from "./components/ShareProjects";
import { ShareSamplesList } from "./components/ShareSamplesList";
import { setStep } from "./services/rootReducer";
import store from "./store";

const { useBreakpoint } = Grid;

function ShareSamples({ projectId, ...params }) {
  const dispatch = useDispatch();
  const screens = useBreakpoint();
  const paths = React.useMemo(
    () => ["projects", "samples", "fields", "finish"],
    []
  );
  const { samples, projectId: sharedProjectId, step } = useSelector(
    (state) => state.reducer
  );

  const clearAndReturn = () => {
    sessionStorage.removeItem("share");
    window.location.href = setBaseUrl(`/projects/${projectId}`);
  };

  // Check to make sure we are entering at samples step
  React.useEffect(() => {
    dispatch(setStep(paths.indexOf(params["*"])));
  }, [dispatch, params, paths]);

  React.useEffect(() => {
    if (
      !sharedProjectId ||
      Number(sharedProjectId) !== Number(projectId) ||
      samples === undefined
    ) {
      clearAndReturn();
    }
  }, [projectId, samples, sharedProjectId]);

  return (
    <Row gutter={[16, 16]}>
      <Col xs={24}>
        <Steps
          direction={screens.md ? "horizontal" : "vertical"}
          current={step}
          // onChange={(key) => navigate(paths[key])}
        >
          <Steps.Step title={"DESTINATION PROJECT"} />
          <Steps.Step title={"REVIEW SAMPLES"} />
          <Steps.Step title={"CHECK METADATA FIELD RESTRICTIONS"} />
        </Steps>
      </Col>
      <Col xs={24}>
        <Router>
          <ShareProjects path="/projects" />
          <ShareSamplesList samples={samples} path="/samples" />
          <ShareMetadataFields path="/fields" />
          <ShareFinish path="/finish" />
        </Router>
      </Col>
    </Row>
  );
}

render(
  <Provider store={store}>
    <Router>
      <ShareSamples path={setBaseUrl(`/projects/:projectId/samples-share/*`)} />
    </Router>
  </Provider>,
  document.querySelector("#root")
);

import { navigate, Router } from "@reach/router";
import { Col, Grid, Row, Steps } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useDispatch, useSelector } from "react-redux";
import { useGetProjectsManagedByUserQuery } from "../../../apis/projects/projects";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareMetadataFields } from "./components/ShareMetadataFields";
import { ShareProjects } from "./components/ShareProjects";
import { ShareSamplesList } from "./components/ShareSamplesList";
import { setDestinationProject, setStep } from "./services/rootReducer";
import store from "./store";

const { useBreakpoint } = Grid;

function ShareSamples({ projectId, ...params }) {
  const dispatch = useDispatch();
  const screens = useBreakpoint();
  const paths = React.useMemo(() => ["projects", "samples", "fields"], []);
  const { samples, projectId: sharedProjectId, step } = useSelector(
    (state) => state.reducer
  );

  const clearAndReturn = () => {
    sessionStorage.removeItem("share");
    window.location.href = setBaseUrl(`/projects/${projectId}`);
  };

  const { data: projects } = useGetProjectsManagedByUserQuery(projectId);

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

  React.useEffect(() => {
    if (projects) {
      // Once we have the projects, we need to set a default shared project,
      // let's just use the first one.
      dispatch(setDestinationProject(projects[0].identifier));
    }
  }, [dispatch, projects]);

  return (
    <Row gutter={[16, 16]}>
      <Col xs={24}>
        <Steps
          direction={screens.md ? "horizontal" : "vertical"}
          current={step}
          onChange={(key) => navigate(paths[key])}
        >
          <Steps.Step title={"DESTINATION PROJECT"} />
          <Steps.Step title={"REVIEW SAMPLES"} />
          <Steps.Step title={"CHECK METADATA FIELD RESTRICTIONS"} />
        </Steps>
      </Col>
      <Col xs={24}>
        <Router>
          <ShareProjects path="/projects" projects={projects} />
          <ShareSamplesList samples={samples} path="/samples" />
          <ShareMetadataFields path="/fields" />
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

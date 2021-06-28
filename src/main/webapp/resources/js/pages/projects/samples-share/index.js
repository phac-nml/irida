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
import { setDestinationProject } from "./services/rootReducer";
import store from "./store";

const { useBreakpoint } = Grid;

function ShareSamples({ projectId, ...params }) {
  const dispatch = useDispatch();
  const screens = useBreakpoint();
  const paths = ["samples", "projects", "fields"];
  const [step, setStep] = React.useState(() => paths.indexOf(params["*"]));
  const { samples, projectId: sharedProjectId } = useSelector(
    (state) => state.reducer
  );

  const clearAndReturn = () => {
    sessionStorage.removeItem("share");
    window.location.href = setBaseUrl(`/projects/${projectId}`);
  };

  const { data: projects } = useGetProjectsManagedByUserQuery(projectId);

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

  const onChange = (key) => navigate(paths[key]).then(() => setStep(key));

  return (
    <Row gutter={[16, 16]}>
      <Col xs={24}>
        <Steps
          direction={screens.md ? "horizontal" : "vertical"}
          current={step}
          onChange={onChange}
        >
          <Steps.Step
            title={"SAMPLES"}
            description={"Review samplebe copied to the destination project"}
          />
          <Steps.Step
            title={"PROJECT"}
            description={"Select the destincation project"}
          />
          <Steps.Step title={"METADATA FIELDS"} />
        </Steps>
      </Col>
      <Col xs={24}>
        <Router>
          <ShareSamplesList samples={samples} path="/samples" />
          <ShareProjects path="/projects" projects={projects} />
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

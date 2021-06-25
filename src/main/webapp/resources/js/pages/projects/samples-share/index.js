import { navigate, Router } from "@reach/router";
import { Layout, Steps } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useSelector } from "react-redux";
import { useGetProjectsManagedByUserQuery } from "../../../apis/projects/projects";
import { grey1 } from "../../../styles/colors";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareMetadataFields } from "./components/ShareMetadataFields";
import { ShareProjects } from "./components/ShareProjects";
import { ShareSamplesList } from "./components/ShareSamplesList";
import store from "./store";

function ShareSamples({ projectId, ...params }) {
  const paths = ["samples", "projects", "fields"];
  const [step, setStep] = React.useState(() => paths.indexOf(params["*"]));
  const { samples, projectId: sharedProjectId } = useSelector(
    (state) => state.reducer
  );

  const { data: projects } = useGetProjectsManagedByUserQuery(projectId);
  const [destinationId, setDestinationId] = React.useState();

  React.useEffect(() => {
    if (!sharedProjectId) {
      window.location.href = setBaseUrl(`/projects/${projectId}`);
    }
    if (
      Number(sharedProjectId) !== Number(projectId) ||
      samples === undefined
    ) {
      sessionStorage.removeItem("share");
      window.location.href = setBaseUrl(`/projects/${projectId}`);
    }
  }, [projectId, samples, sharedProjectId]);

  React.useEffect(() => {
    if (projects) {
      // Once we have the projects, we need to set a default shared project,
      // let's just use the first one.
      setDestinationId(projects[0].identifier);
    }
  }, [projects]);

  const onChange = (key) => navigate(paths[key]).then(() => setStep(key));

  return (
    <Layout style={{ backgroundColor: grey1 }}>
      <Layout.Sider width={200} style={{ backgroundColor: grey1 }}>
        <Steps
          direction="vertical"
          current={step}
          style={{ height: 600 }}
          onChange={onChange}
        >
          <Steps.Step title={"SAMPLES"} />
          <Steps.Step title={"PROJECT"} />
          <Steps.Step title={"METADATA FIELDS"} />
        </Steps>
      </Layout.Sider>
      <Layout.Content>
        <Router>
          <ShareSamplesList samples={samples} path="/samples" />
          <ShareProjects
            path="/projects"
            projects={projects}
            setDestinationId={setDestinationId}
            destinationId={destinationId}
          />
          <ShareMetadataFields
            path="/fields"
            projectId={projectId}
            sharedProjectId={destinationId}
          />
        </Router>
      </Layout.Content>
    </Layout>
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

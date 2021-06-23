import { navigate, Router } from "@reach/router";
import { Layout, Steps } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
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

  const { data: projects } = useGetProjectsManagedByUserQuery(projectId);
  const [shareProjectId, setShareProjectId] = React.useState();
  const [samples, setSamples] = React.useState([]);

  React.useEffect(() => {
    const sharedString = sessionStorage.getItem("share");
    if (!sharedString) {
      window.location.href = setBaseUrl(`/projects/${projectId}`);
    }
    const shared = JSON.parse(sharedString);
    if (
      Number(shared.projectId) !== Number(projectId) ||
      shared.samples === undefined
    ) {
      sessionStorage.removeItem("share");
      window.location.href = setBaseUrl(`/projects/${projectId}`);
    }
    setSamples(shared.samples);
  }, [projectId]);

  React.useEffect(() => {
    if (projects) {
      // ONce we have the projects, we need to set a default shared project,
      // let's just use the first one.
      setShareProjectId(projects[0].identifier);
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
            setShareProjectId={setShareProjectId}
            shareProjectId={shareProjectId}
          />
          <ShareMetadataFields
            path="/fields"
            projectId={projectId}
            sharedProjectId={shareProjectId}
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

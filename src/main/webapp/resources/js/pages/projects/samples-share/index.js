import { navigate, Router } from "@reach/router";
import { Layout, Steps } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useSelector } from "react-redux";
import { grey1 } from "../../../styles/colors";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareMetadataFields } from "./components/ShareMetadataFields";
import { ShareProjects } from "./components/ShareProjects";
import { ShareSamplesList } from "./components/ShareSamplesList";
import store from "./store";

function ShareSamples({ projectId, ...params }) {
  const paths = ["samples", "projects", "fields"];
  const [step, setStep] = React.useState(paths.indexOf(params["*"]));
  const { samples, projectId: sharedProjectId } = useSelector(
    (state) => state.sharedSamples
  );

  if (Number(sharedProjectId) !== Number(projectId) || samples === undefined) {
    sessionStorage.removeItem("share");
    window.location.href = setBaseUrl(`/projects/${projectId}`);
  }

  const handleOnLoad = ({ startIndex, stopIndex }) => {};
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
          <ShareProjects path="/projects" />
          <ShareMetadataFields path="/fields" />
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

import { navigate, Router } from "@reach/router";
import { Layout, Steps } from "antd";
import React from "react";
import { render } from "react-dom";
import { grey1 } from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareMetadataFields } from "./components/ShareMetadataFields";
import { ShareProjects } from "./components/ShareProjects";
import { ShareSamplesList } from "./components/ShareSamplesList";

function ShareSamples({ projectId, ...params }) {
  const paths = ["samples", "projects", "fields"];
  const [step, setStep] = React.useState(paths.indexOf(params["*"]));
  const [sampleIds, setSampleIds] = React.useState();
  const [samples, setSamples] = React.useState();

  React.useEffect(() => {
    const shared = sessionStorage.getItem("share");
    if (shared) {
      const sharedJson = JSON.parse(shared);
      if (Number(sharedJson.projectId) !== Number(projectId)) {
        sessionStorage.removeItem("share");
        window.location.href = setBaseUrl(`/projects/${projectId}`);
      }
      setSampleIds(sharedJson.ids);

      // TODO: Move this into redux?
      const params = new URLSearchParams();
      params.set("projectId", projectId);
      params.set("ids", sharedJson.ids);
      fetch(`${setBaseUrl(`/ajax/samples/share`)}?${params.toString()}`)
        .then((response) => response.json())
        .then((data) => setSamples(data));

      // sessionStorage.removeItem("share");
    } else {
      window.location.href = setBaseUrl(`/projects/${projectId}`);
    }
  }, [projectId]);

  const handleOnLoad = ({ startIndex, stopIndex }) => {};
  const onChange = (key) => navigate(paths[key]).then(() => setStep(key));

  return (
    <Layout style={{ backgroundColor: grey1 }}>
      <Layout.Sider width={200} style={{ backgroundColor: grey1 }}>
        <Steps
          direction="vertical"
          current={step}
          style={{ height: `100%` }}
          onChange={onChange}
        >
          <Steps.Step title={"SAMPLES"} />
          <Steps.Step title={"PROJECT"} />
          <Steps.Step title={"METADATA FIELDS"} />
        </Steps>
      </Layout.Sider>
      <Layout.Content style={{ padding: SPACE_MD }}>
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
  <Router>
    <ShareSamples path={setBaseUrl(`/projects/:projectId/samples-share/*`)} />
  </Router>,
  document.querySelector("#root")
);

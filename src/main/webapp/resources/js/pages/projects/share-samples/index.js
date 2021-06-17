import { Router } from "@reach/router";
import { List } from "antd";
import React from "react";
import { render } from "react-dom";
import { setBaseUrl } from "../../../utilities/url-utilities";

function ShareSamples({ projectId }) {
  console.log("HELO");
  React.useEffect(() => {
    const shared = sessionStorage.getItem("share");
    if (shared) {
      const sharedJson = JSON.parse(shared);
      if (Number(sharedJson.projectId) !== Number(projectId)) {
        sessionStorage.removeItem("share");
        window.location.href = setBaseUrl(`/projects/${projectId}`);
      }

      sessionStorage.removeItem("share");
    } else {
      window.location.href = setBaseUrl(`/projects/${projectId}`);
    }
  }, [projectId]);
  // const location = useLocation();

  // React.useEffect(() => {
  //   const params = new URLSearchParams(location.search);
  //   const ids = params.getAll("id");
  //
  //   // If there are no ids, then redirect to the samples page.
  //   if (!ids) window.location.href = setBaseUrl(`projects/${projectId}`);
  // }, [location.search, projectId]);

  const handleLoad = ({ startIndex, stopIndex }) => {};

  return <List>HELLO JIMMY</List>;
}

render(
  <Router>
    <ShareSamples path={setBaseUrl(`/projects/:projectId/samples/share`)} />
  </Router>,
  document.querySelector("#root")
);

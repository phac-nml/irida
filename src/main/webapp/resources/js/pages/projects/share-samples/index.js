import { Router, useLocation } from "@reach/router";
import React from "react";
import { render } from "react-dom";
import { setBaseUrl } from "../../../utilities/url-utilities";

function ShareSamples({ projectId }) {
  // const location = useLocation();

  // React.useEffect(() => {
  //   const params = new URLSearchParams(location.search);
  //   const ids = params.getAll("id");
  //
  //   // If there are no ids, then redirect to the samples page.
  //   if (!ids) window.location.href = setBaseUrl(`projects/${projectId}`);
  // }, [location.search, projectId]);

  return <div>HELO JIMMY</div>;
}

render(
  <Router>
    <ShareSamples path={setBaseUrl(`/projects/:projectId/samples/share`)} />
  </Router>,
  document.querySelector("#root")
);

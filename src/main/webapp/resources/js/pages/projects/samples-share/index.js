import { Router } from "@reach/router";
import React from "react";
import { render } from "react-dom";
import { setBaseUrl } from "../../../utilities/url-utilities";

/**
 * React component for the layout of the share samples page.
 *
 * @param {number} projectId - identifier for the current project
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareSamplesLayout({ projectId }) {
  const [samples, setSamples] = React.useState([]);

  React.useEffect(() => {
    /*
    When this component is rendered we get the original data out of session
     */
    const dataString = window.sessionStorage.getItem(`share-${projectId}`);
    if (!dataString) {
      window.location.href = setBaseUrl(`/projects/${projectId}`);
    }
    const dataJson = JSON.parse(dataString);
    // TODO: Use redux to store this data
    setSamples(dataJson);
  }, [projectId]);

  return (
    <>
      <p>
        This is just a placeholder and a check to make sure that the samples
        have been retrieved from storage
      </p>
      <ul>
        {samples.map((sample) => (
          <li key={sample.name}>{sample.name}</li>
        ))}
      </ul>
    </>
  );
}

render(
  <Router>
    <ShareSamplesLayout
      path={setBaseUrl(`/projects/:projectId/samples-share`)}
    />
  </Router>,
  document.querySelector("#root")
);

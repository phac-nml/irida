import { Button } from "antd";
import React from "react";
import { render } from "react-dom";
import { IconShare } from "../../../components/icons/Icons";
import { grey9 } from "../../../styles/colors";
import {
  setBaseUrl,
  getProjectIdFromUrl,
} from "../../../utilities/url-utilities";

/**
 * React component to render a link on the project samples page > sample tools
 * menu to share samples with another project.  This stores the samples to
 * session storage in the browser to be consumed on the share page.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareSamplesLink() {
  function handleClick() {
    const selected = window.$dt.select.selected()[0];

    if (selected.size === 0) return;

    const samples = [];
    selected.forEach(
      ({ id, sampleName: name, owner, projectName, projectId }) => {
        samples.push({ id, name, owner, projectName, projectId });
      }
    );

    // Get the project id from the url
    const projectId = getProjectIdFromUrl();

    // Store them to window storage for later use.
    window.sessionStorage.setItem(
      "share",
      JSON.stringify({
        samples,
        projectId,
        timestamp: Date.now(),
      })
    );

    // Redirect user to share page
    window.location.href = setBaseUrl(`/projects/${projectId}/share`);
  }

  return (
    <Button
      className="t-share-btn"
      type="link"
      style={{
        margin: `0 inherit`,
        padding: 0,
        paddingLeft: 20,
        color: grey9,
      }}
      icon={<IconShare style={{ marginRight: 3 }} />}
      onClick={handleClick}
    >
      {i18n("project.samples.nav.share")}
    </Button>
  );
}

const querySelector = document.querySelector(".js-share-samples");

if (querySelector) {
  render(<ShareSamplesLink />, querySelector);
}

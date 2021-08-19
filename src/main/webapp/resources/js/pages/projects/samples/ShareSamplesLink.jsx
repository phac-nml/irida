import { Button } from "antd";
import React from "react";
import { render } from "react-dom";
import { IconShare } from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";

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
    selected.forEach(({ id, sampleName: name, owner }) => {
      samples.push({ id, name, owner });
    });

    // Store them to window storage for later use.
    const projectId = selected.values().next().value.projectId;
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
      icon={<IconShare />}
      onClick={handleClick}
    >
      {i18n("project.samples.nav.share")}
    </Button>
  );
}

const querySelector = document.querySelector(".js-share-samples");
console.log(querySelector);
if (querySelector) {
  render(<ShareSamplesLink />, querySelector);
}

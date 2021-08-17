import { Button } from "antd";
import React from "react";
import { render } from "react-dom";
import { IconShare } from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";

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
    <Button type="link" icon={<IconShare />} onClick={handleClick}>
      SHARE
    </Button>
  );
}

render(<ShareSamplesLink />, document.querySelector(".js-share-samples"));

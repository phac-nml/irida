import { Button } from "antd";
import React from "react";
import { render } from "react-dom";
import { IconShare } from "../../../../components/icons/Icons";
import { grey9 } from "../../../../styles/colors";
import { setBaseUrl } from "../../../../utilities/url-utilities";

/**
 * React component to render a button to link to the share samples page.
 * To speed this up, it saves the selected samples to session storage for
 * performance issues.
 * @returns {JSX.Element}
 * @constructor
 */
export function SamplesShareButton() {
  const shareSamples = () => {
    const selected = window.$dt.select.selected()[0];
    const projectId = selected.values().next().value.projectId;
    const samples = [];
    selected.forEach(({ id, sampleName: name, owner }) => {
      samples.push({
        id,
        name,
        owner,
      });
    });

    /*
    We are going to store the sample information into session storage since it can
    become massive request and thn data is already present on the client.
     */
    if (samples.length) {
      window.sessionStorage.setItem(
        `share-${projectId}`,
        JSON.stringify(samples)
      );
      window.location.href = setBaseUrl(`/projects/${projectId}/samples-share`);
    }
  };

  return (
    <Button
      type="link"
      icon={<IconShare style={{ marginRight: 3, color: grey9 }} />}
      style={{
        margin: `0 inherit`,
        padding: 0,
        paddingLeft: 20,
        color: grey9,
      }}
      onClick={shareSamples}
    >
      {i18n("SamplesShareButton.label")}
    </Button>
  );
}

render(<SamplesShareButton />, document.querySelector(".js-samples-share"));

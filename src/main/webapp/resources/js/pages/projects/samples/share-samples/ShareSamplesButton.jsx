import { Button } from "antd";
import React from "react";
import { render } from "react-dom";
import { IconShareSamples } from "../../../../components/icons/Icons";
import ShareSamplesWrapper from "../../../../components/samples/share";

export function ShareSamplesButton() {
  const getSelectedSamples = () => {
    const selected = window.$dt.rows({ selected: true }).data();
    const samples = [];
    for (let i = 0; i < selected.length; i++) {
      samples.push(selected[i]);
    }
    return samples;
  };

  return (
    <ShareSamplesWrapper getSelectedSamples={getSelectedSamples}>
      <Button icon={<IconShareSamples />} type="link">
        SHARE SAMPLES
      </Button>
    </ShareSamplesWrapper>
  );
}

// Check to make sure this node is there.  If not, the user cannot
// copy samples, therefore this does not need to get rendered.
const elm = document.querySelector(".js-share-samples");
if (elm) {
  render(<ShareSamplesButton />, elm);
}

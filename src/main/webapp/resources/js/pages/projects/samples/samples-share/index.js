import { Button } from "antd";
import React from "react";
import { render } from "react-dom";
import { IconShare } from "../../../../components/icons/Icons";

export function SamplesShareButton() {
  const shareSamples = () => {
    const selected = window.$dt.select.selected()[0];
    const samples = [];
    selected.forEach(({ id, sampleName: name, owner }) => {
      samples.push({
        id,
        name,
        owner,
      });
    });
    console.log(samples);
  };

  return (
    <Button type="link" icon={<IconShare />} onClick={shareSamples}>
      {i18n("SamplesShareButton.label")}
    </Button>
  );
}

render(<SamplesShareButton />, document.querySelector(".js-samples-share"));

import { Button } from "antd";
import React from "react";
import { render } from "react-dom";
import { IconShare } from "../../../components/icons/Icons";

export function SamplesShareButton() {
  return (
    <Button type="link" icon={<IconShare />}>
      Share Samples
    </Button>
  );
}

render(
  <h1>Share Samples with Another Project</h1>,
  document.querySelector("#root")
);

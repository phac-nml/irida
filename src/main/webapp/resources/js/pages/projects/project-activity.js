import { Typography } from "antd";
import React from "react";
import { render } from "react-dom";

function ProjectActivity() {
  return (
    <>
      <Typography.Title level={2}>Project Activity</Typography.Title>
    </>
  );
}

render(<ProjectActivity />, document.querySelector("#root"));

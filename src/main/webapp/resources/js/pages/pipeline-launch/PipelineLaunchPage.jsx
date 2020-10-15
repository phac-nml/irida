import React from "react";
import { LaunchContent } from "./LaunchContent";
import { Tabs, Typography } from "antd";
import { useLaunchState } from "./launch-context";

export function PipelineLaunchPage({ pipelineId }) {
  const { pipelineName } = useLaunchState();

  const [current, setCurrent] = React.useState(0);

  // Update the page title with the current pipeline.
  const title = i18n("LaunchContent.title", pipelineName);
  document.title = title;

  return (
    <>
      <Typography.Title>{title}</Typography.Title>
      <Tabs>
        <Tabs.TabPane tab={"PARAMETERS"} key="parameters">
          <LaunchContent />
        </Tabs.TabPane>
        <Tabs.TabPane tab={"FILES"} key="files">
          <div>I AM A BUNCH OF FILES</div>
        </Tabs.TabPane>
      </Tabs>
    </>
  );
}

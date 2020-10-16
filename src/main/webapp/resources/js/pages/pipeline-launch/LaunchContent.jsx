import React from "react";
import { Card, Space } from "antd";
import { PipelineResultsSharing } from "./PipelineResultsSharing";
import { PipelineRequiredParameters } from "./PipelineRequiredParameters";
import { PipelineParametersWithOptions } from "./PipelineParametersWithOptions";
import { PipelineDynamicParameters } from "./PipelineDynamicParameters";

export function LaunchContent() {
  return (
    <section>
      <Space direction="vertical" size="middle" style={{ width: "100%" }}>
        <Card title={i18n("LaunchContent.parameters")}>
          <PipelineRequiredParameters />
          <PipelineParametersWithOptions />
          <PipelineDynamicParameters />
        </Card>
      </Space>
    </section>
  );
}

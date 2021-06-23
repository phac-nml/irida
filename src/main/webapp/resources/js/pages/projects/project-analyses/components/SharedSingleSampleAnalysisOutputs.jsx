import React from "react";
import { Typography } from "antd";

import { useGetSharedSingleSampleAnalysisOutputsQuery } from "../../../../apis/projects/analyses";

import SingleSampleAnalysisOutputs from "./SingleSampleAnalysisOutputs";

const { Title } = Typography;

export default function SharedSingleSampleAnalysisOutputs({ projectId }) {
  const {
    data: sharedSingleSampleAnalysisOutputs = {},
    isLoading,
  } = useGetSharedSingleSampleAnalysisOutputsQuery(projectId);

  return (
    <>
      <Title level={2}>Shared Single Sample Analysis Outputs</Title>
      <SingleSampleAnalysisOutputs
        outputs={sharedSingleSampleAnalysisOutputs}
        isLoading={isLoading}
      />
    </>
  );
}

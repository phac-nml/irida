import React from "react";
import { Typography } from "antd";
import SingleSampleAnalysisOutputs from "./SingleSampleAnalysisOutputs";
import { useGetAutomatedSingleSampleAnalysisOutputsQuery } from "../../../../apis/projects/analyses";
const { Title } = Typography;

export default function AutomatedSingleSampleAnalysisOutputs({ projectId }) {
  const {
    data: automatedSingleSampleAnalysisOutputs = {},
    isLoading,
  } = useGetAutomatedSingleSampleAnalysisOutputsQuery(projectId);

  return (
    <>
      <Title level={2}>
        {i18n("AutomatedSingleSampleAnalysisOutputs.title")}
      </Title>
      <SingleSampleAnalysisOutputs
        outputs={automatedSingleSampleAnalysisOutputs}
        isLoading={isLoading}
        projectId={projectId}
      />
    </>
  );
}

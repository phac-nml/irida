import React from "react";
import { Typography } from "antd";
import SingleSampleAnalysisOutputs from "../../../../components/analyses-outputs/SingleSampleAnalysisOutputs";
import { useGetAutomatedSingleSampleAnalysisOutputsQuery } from "../../../../apis/analyses/analyses";
const { Title } = Typography;

/**
 * React component for getting the shared single sample analysis outputs
 * and setting the layout for the page
 * @param projectId The project identifier
 * @returns {JSX.Element}
 * @constructor
 */

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

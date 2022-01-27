import { Typography } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import { useGetAutomatedSingleSampleAnalysisOutputsQuery } from "../../../../apis/analyses/analyses";
import SingleSampleAnalysisOutputs from "../../../../components/analyses-outputs/SingleSampleAnalysisOutputs";

const { Title } = Typography;

/**
 * React component for getting the automated single sample analysis outputs
 * and setting the layout for the page
 * @param projectId The project identifier
 * @returns {JSX.Element}
 * @constructor
 */

export default function AutomatedSingleSampleAnalysisOutputs() {
  const { projectId } = useParams();
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

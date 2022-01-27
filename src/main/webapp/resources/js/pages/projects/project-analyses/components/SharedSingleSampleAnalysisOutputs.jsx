import { Typography } from "antd";
import React from "react";
import { useParams } from "react-router-dom";

import { useGetSharedSingleSampleAnalysisOutputsQuery } from "../../../../apis/analyses/analyses";

import SingleSampleAnalysisOutputs from "../../../../components/analyses-outputs/SingleSampleAnalysisOutputs";

const { Title } = Typography;

/**
 * React component for getting the shared single sample analysis outputs
 * and setting the layout for the page
 * @param projectId The project identifier
 * @returns {JSX.Element}
 * @constructor
 */

export default function SharedSingleSampleAnalysisOutputs() {
  const { projectId } = useParams();
  const {
    data: sharedSingleSampleAnalysisOutputs = {},
    isLoading,
  } = useGetSharedSingleSampleAnalysisOutputsQuery(projectId);

  return (
    <>
      <Title level={2}>{i18n("SharedSingleSampleAnalysisOutputs.title")}</Title>
      <SingleSampleAnalysisOutputs
        outputs={sharedSingleSampleAnalysisOutputs}
        isLoading={isLoading}
        projectId={projectId}
      />
    </>
  );
};

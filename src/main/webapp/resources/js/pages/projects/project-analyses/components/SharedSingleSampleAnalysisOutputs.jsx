import React from "react";
import { Typography } from "antd";

import { useGetSharedSingleSampleAnalysisOutputsQuery } from "../../../../apis/projects/analyses";

import SingleSampleAnalysisOutputs from "./SingleSampleAnalysisOutputs";

const { Title } = Typography;

/**
 * React component for getting the shared single sample analysis outputs
 * and setting the layout for the page
 * @param projectId The project identifier
 * @returns {JSX.Element}
 * @constructor
 */

export default function SharedSingleSampleAnalysisOutputs({ projectId }) {
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
      />
    </>
  );
}

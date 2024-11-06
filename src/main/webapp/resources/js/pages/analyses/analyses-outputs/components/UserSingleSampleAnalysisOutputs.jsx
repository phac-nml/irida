import React from "react";
import SingleSampleAnalysisOutputs from "../../../../components/analyses-outputs/SingleSampleAnalysisOutputs";
import { useGetUserSingleSampleAnalysisOutputsQuery } from "../../../../apis/analyses/analyses";
import { PageWrapper } from "../../../../components/page/PageWrapper";

/**
 * React component for getting the yser single sample analysis outputs
 * and setting the layout for the page
 * @returns {JSX.Element}
 * @constructor
 */

export default function UserSingleSampleAnalysisOutputs() {
  const { data: userSingleSampleAnalysisOutputs = {}, isLoading } =
    useGetUserSingleSampleAnalysisOutputsQuery();

  return (
    <PageWrapper title={i18n("UserSingleSampleAnalysisOutputs.title")}>
      <SingleSampleAnalysisOutputs
        outputs={userSingleSampleAnalysisOutputs}
        isLoading={isLoading}
      />
    </PageWrapper>
  );
}

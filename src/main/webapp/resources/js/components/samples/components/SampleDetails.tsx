import React, { Suspense } from "react";
import { ViewerTab } from "./SampleDetailsModal";
import { ContentLoading } from "../../loader";

const SampleInfo = React.lazy(() => import("./SampleInfo"));
const SampleMetadata = React.lazy(() => import("./SampleMetadata"));
const SampleFiles = React.lazy(() => import("./SampleFiles"));
const SampleAnalyses = React.lazy(() => import("./SampleAnalyses"));

export interface SampleDetailsProps {
  component: ViewerTab;
}

/**
 * React component to render the details of a sample, including metadata
 * and files.
 *
 * @param details - The sample details
 * @returns {JSX.Element}
 * @constructor
 */
export default function SampleDetails({
  component,
}: SampleDetailsProps): JSX.Element {
  switch (component) {
    case "metadata":
      return (
        <Suspense
          fallback={
            <ContentLoading message={i18n("SampleDetails.metadata.loading")} />
          }
        >
          <SampleMetadata />
        </Suspense>
      );
    case "files":
      return (
        <Suspense
          fallback={
            <ContentLoading message={i18n("SampleDetails.files.loading")} />
          }
        >
          <SampleFiles />
        </Suspense>
      );
    case "analyses":
      return (
        <Suspense
          fallback={
            <ContentLoading message={i18n("SampleDetails.analyses.loading")} />
          }
        >
          <SampleAnalyses />
        </Suspense>
      );
    default:
      return (
        <Suspense
          fallback={
            <ContentLoading message={i18n("SampleDetails.details.loading")} />
          }
        >
          <SampleInfo />
        </Suspense>
      );
  }
}

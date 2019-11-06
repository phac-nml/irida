/*
 * This file get the sample data from the server, and loads
 * the Samples and Reference File components
 */

/*
 * The following import statements makes available all the elements
 * required by the component
 */
import React, { useContext } from "react";
import { AnalysisSamplesContext } from "../../../../contexts/AnalysisSamplesContext";
import { getI18N } from "../../../../utilities/i18n-utilties";
import { AnalysisReferenceFileRenderer } from "./AnalysisReferenceFileRenderer";
import { AnalysisSampleRenderer } from "./AnalysisSampleRenderer";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function AnalysisSamples() {
  /*
   * The following const statement
   * make the required context which contains
   * the state and methods available to the component
   */
  const { analysisSamplesContext } = useContext(AnalysisSamplesContext);

  /*
   * If there is a reference file the the AnalysisReferenceFileRenderer
   * component is rendered. If there are samples which were
   * used in the analysis then the AnalysisSamplesRenderer is
   * rendered
   */
  return (
    <TabPaneContent title={getI18N("AnalysisSamples.samples")}>
      {analysisSamplesContext.referenceFile ? (
        <AnalysisReferenceFileRenderer />
      ) : null}

      <AnalysisSampleRenderer />
    </TabPaneContent>
  );
}

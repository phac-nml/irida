/*
 * This file get the sample data from the server, and loads
 * the Samples and Reference File components
 */

/*
 * The following import statements makes available all the elements
 * required by the component
 */
import React, { useContext, useEffect, useState } from "react";
import { Card, Row, Col, Icon, Button, Typography } from "antd";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { AnalysisReferenceFileRenderer } from "./AnalysisReferenceFileRenderer";
import { AnalysisSampleRenderer } from "./AnalysisSampleRenderer";

const { Title } = Typography;

export default function AnalysisSamples() {
  /*
   * The following const statement
   * make the required context which contains
   * the state and methods available to the component
   */
  const { analysisDetailsContext, loadAnalysisSamples } = useContext(
    AnalysisDetailsContext
  );

  // Load the analysis samples data on load
  useEffect(() => {
    loadAnalysisSamples();
  }, []);

  /*
   * If there is a reference file the the AnalysisReferenceFileRenderer
   * component is rendered. If there are samples which were
   * used in the analysis then the AnalysisSamplesRenderer is
   * rendered
   */
  return (
    <>
      <Title level={2}>{getI18N("analysis.tab.samples")}</Title>
      {analysisDetailsContext.referenceFile ? (
        <AnalysisReferenceFileRenderer />
      ) : null}

      {analysisDetailsContext.samples.length > 0 ? (
        <AnalysisSampleRenderer />
      ) : null}
    </>
  );
}

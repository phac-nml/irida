import React, { useContext, useEffect, useState } from "react";
import { Card, Row, Col, Icon, Button, Typography } from "antd";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { AnalysisReferenceFileRenderer } from "./AnalysisReferenceFileRenderer";
import { AnalysisSampleRenderer } from "./AnalysisSampleRenderer";

const { Title } = Typography;

export default function AnalysisSamples() {
  const { analysisDetailsContext, loadAnalysisSamples } = useContext(
    AnalysisDetailsContext
  );

  useEffect(() => {
    loadAnalysisSamples();
  }, []);

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

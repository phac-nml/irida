import React, { useContext, useEffect, useState } from "react";
import { Card, Row, Col, Icon, Button, Typography } from "antd";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { FONT_WEIGHT_DEFAULT } from "../../../styles/fonts";
import { SPACE_XS, SPACE_LG } from "../../../styles/spacing";

const { Title } = Typography;

export default function AnalysisSamples() {
  const { analysisDetailsContext, loadAnalysisSamples } = useContext(
    AnalysisDetailsContext
  );

  useEffect(() => {
    loadAnalysisSamples();
  }, []);

  const downloadReferenceFile = () => {
    if (analysisDetailsContext.referenceFile.identifier !== undefined) {
      window.open(
        `${window.TL.BASE_URL}referenceFiles/download/${analysisDetailsContext.referenceFile.identifier}`,
        "_blank"
      );
    }
  };

  const renderReferenceFile = () => {
    const referenceFile = [];

    if (analysisDetailsContext.referenceFile.length === 0) {
      return null;
    } else {
      referenceFile.push(
        <div style={{ marginBottom: SPACE_LG }} key="samplesDiv-1">
          <h4
            style={{ fontWeight: FONT_WEIGHT_DEFAULT }}
            key="reference-file-heading-1"
          >
            {getI18N("analysis.tab.content.samples.reference-file")}
          </h4>
          <Row key="row-reference-file-1">
            <span key="reference-file-1">
              {analysisDetailsContext.referenceFile.label}
            </span>
            <Button
              key="reference-file-1-download-button"
              className="pull-right"
              style={{ marginTop: SPACE_XS }}
              style={{ marginTop: SPACE_XS }}
              type="primary"
              onClick={() => {
                downloadReferenceFile();
              }}
            >
              <Icon type="download" />{" "}
              {getI18N("analysis.tab.content.samples.download-reference-file")}
            </Button>
          </Row>
        </div>
      );
      return referenceFile;
    }
  };

  const renderSamples = () => {
    const samplesList = [];

    if (analysisDetailsContext.sequenceFilePairList.length > 0) {
      let pairIndex = 0;
      for (const [
        index,
        sampleObj
      ] of analysisDetailsContext.samples.entries()) {
        samplesList.push(
          <Card
            type="inner"
            title={
              <Button
                type="link"
                href={`${window.TL.BASE_URL}samples/${sampleObj.sample.identifier}/details`}
                target="_blank"
              >
                <Icon type="filter" rotate="180" />{" "}
                {sampleObj.sample.sampleName}
              </Button>
            }
            key={`sampleId-${sampleObj.sample.identifier}`}
          >
            <Row
              key={`fileId-${analysisDetailsContext.sequenceFilePairList[pairIndex].identifier}`}
              type="flex"
            >
              <Col span={12}>
                <Button
                  type="link"
                  target="_blank"
                  href={`${window.TL.BASE_URL}sequenceFiles/${sampleObj.sequenceFilePair.identifier}/file/${analysisDetailsContext.sequenceFilePairList[pairIndex].identifier}/summary`}
                >
                  <Icon type="arrow-right" />{" "}
                  {analysisDetailsContext.sequenceFilePairList[pairIndex].label}
                </Button>
              </Col>
              <Col span={12} style={{ textAlign: "right" }}>
                {analysisDetailsContext.sequenceFileSizeList[pairIndex]}
              </Col>
            </Row>
            <Row
              key={`fileId-${analysisDetailsContext.sequenceFilePairList[pairIndex + 1].identifier}`}
              type="flex"
            >
              <Col span={12}>
                <Button
                  type="link"
                  target="_blank"
                  href={`${window.TL.BASE_URL}sequenceFiles/${sampleObj.sequenceFilePair.identifier}/file/${analysisDetailsContext.sequenceFilePairList[pairIndex + 1].identifier}/summary`}
                >
                  <Icon type="arrow-left" />{" "}
                  {
                    analysisDetailsContext.sequenceFilePairList[pairIndex + 1]
                      .label
                  }
                </Button>
              </Col>
              <Col span={12} style={{ textAlign: "right" }}>
                {analysisDetailsContext.sequenceFileSizeList[pairIndex + 1]}
              </Col>
            </Row>
          </Card>
        );
        pairIndex = pairIndex + 2;
      }
    } else {
      samplesList.push(
        <p key={`no-paired-end-0`}>
          {getI18N("analysis.input-files.no-paired-end")}
        </p>
      );
    }
    return samplesList;
  };

  return (
    <>
      <Title level={2}>{getI18N("analysis.tab.samples")}</Title>
      {analysisDetailsContext.referenceFile ? renderReferenceFile() : null}

      {analysisDetailsContext.samples.length > 0 ? renderSamples() : null}
    </>
  );
}

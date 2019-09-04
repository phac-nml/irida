import React, { useContext } from "react";
import { getI18N } from "../../../utilities/i18n-utilties";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { SPACE_LG, SPACE_XS } from "../../../styles/spacing";
import { FONT_WEIGHT_DEFAULT } from "../../../styles/fonts";
import { Card, Row, Col, Icon, Button } from "antd";

export function AnalysisSampleRenderer() {
  const { analysisDetailsContext } = useContext(
    AnalysisDetailsContext
  );

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
        {renderSamples()}
    </>
  );
}

/*
 * The following import statements makes available all the elements
 * required by the component
 */

import React, { useContext } from "react";
import { getI18N } from "../../../utilities/i18n-utilties";
import { AnalysisSamplesContext } from "../../../contexts/AnalysisSamplesContext";
import { Card, Row, Col, Icon, Button } from "antd";

export function AnalysisSampleRenderer() {
  /*
   * The following const statement
   * make the required context which contains
   * the state and methods available to the component
   */
  const { analysisSamplesContext } = useContext(AnalysisSamplesContext);

  const renderSamples = () => {
    const samplesList = [];

    if (analysisSamplesContext.sequenceFilePairList.length > 0) {
      let pairIndex = 0;
      for (const [
        index,
        sampleObj
      ] of analysisSamplesContext.samples.entries()) {
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
              key={`fileId-${analysisSamplesContext.sequenceFilePairList[pairIndex].identifier}`}
            >
              <span
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center"
                }}
              >
                <Button
                  type="link"
                  target="_blank"
                  href={`${window.TL.BASE_URL}sequenceFiles/${sampleObj.sequenceFilePair.identifier}/file/${analysisSamplesContext.sequenceFilePairList[pairIndex].identifier}/summary`}
                >
                  <Icon type="arrow-right" />{" "}
                  {analysisSamplesContext.sequenceFilePairList[pairIndex].label}
                </Button>
                <span>
                  {analysisSamplesContext.sequenceFileSizeList[pairIndex]}
                </span>
              </span>
            </Row>
            <Row
              key={`fileId-${analysisSamplesContext.sequenceFilePairList[pairIndex + 1].identifier}`}
            >
              <span
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center"
                }}
              >
                <Button
                  type="link"
                  target="_blank"
                  href={`${window.TL.BASE_URL}sequenceFiles/${sampleObj.sequenceFilePair.identifier}/file/${analysisSamplesContext.sequenceFilePairList[pairIndex + 1].identifier}/summary`}
                >
                  <Icon type="arrow-left" />{" "}
                  {
                    analysisSamplesContext.sequenceFilePairList[pairIndex + 1]
                      .label
                  }
                </Button>
                <span>
                  {analysisSamplesContext.sequenceFileSizeList[pairIndex + 1]}
                </span>
              </span>
            </Row>
          </Card>
        );
        pairIndex = pairIndex + 2;
      }
    } else {
      samplesList.push(
        <p key={`no-paired-end-0`}>{getI18N("AnalysisSamples.noPairedEnd")}</p>
      );
    }
    return samplesList;
  };

  return <>{renderSamples()}</>;
}

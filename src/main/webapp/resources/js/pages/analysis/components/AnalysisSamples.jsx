import React, { useContext, useEffect, useState } from "react";
import { Card, Row } from "antd";
import { AnalysisContext } from "../../../state/AnalysisState";
import { getI18N } from "../../../utilities/i18n-utilties";

import {
    getAnalysisInputFiles,
    getForwardFile,
    getReverseFile
} from "../../../apis/analysis/analysis";

export default function AnalysisSamples() {
  const { state, dispatch } = useContext(AnalysisContext);

  useEffect(() => {
    getAnalysisInputFiles(state.analysis.identifier).then(res => {
      dispatch({ type: "SAMPLES_LIST", samples: res.data.samples });
      dispatch({ type: "SEQUENCE_FILE_PAIR_LIST", sequenceFilePairList: res.data.seqFilePairs });
    });
  }, []);

  const renderSamples = () => {
    const samplesList = [];

    if(state.samples.length > 0 && state.sequenceFilePairList.length > 0) {
        let pairIndex = 0;
        for (let i = 0; i < state.samples.length; i++) {
          samplesList.push(
            <Card
                type="inner"
                title={<a target="_blank" href={`${window.TL.BASE_URL}samples/${state.samples[i].sample.identifier}/details`}>{state.samples[i].sample.sampleName}</a>}
                extra={<a href="#">Details</a>}
                key={`sample${i}`}
            >
                <Row>
                    <a
                        target="_blank"
                        href={`${window.TL.BASE_URL}sequenceFiles/${state.samples[i].sequenceFilePair.identifier}/file/${state.sequenceFilePairList[pairIndex + 1].identifier}/summary`}>{state.sequenceFilePairList[pairIndex].label}
                   </a>
                </Row>
                <Row>
                    <a
                        target="_blank"
                        href={`${window.TL.BASE_URL}sequenceFiles/${state.samples[i].sequenceFilePair.identifier}/file/${state.sequenceFilePairList[pairIndex + 1].identifier}/summary`}>{state.sequenceFilePairList[pairIndex].label}
                    </a>
                </Row>
            </Card>
          );
           pairIndex = pairIndex + 2;
        }
    }
    return samplesList;
  };

  return (
    <>
      <h2 style={{ fontWeight: "bold" }}>{getI18N("analysis.tab.samples")}</h2>
      {
        state.samples.length > 0 ?
            renderSamples() : null
        }
    </>
  );
}

import React, { useContext, useEffect, useState } from "react";
import { Card, Row, Icon, Button } from "antd";
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
      dispatch({
         type: "SAMPLES_DATA",
         samples: res.data.samples,
         sequenceFilePairList: res.data.seqFilePairs,
         referenceFile: res.data.referenceFile,
         sequenceFileSizeList: res.data.seqFilePairSizes,
      });
    });
  }, []);

  const renderReferenceFile = () => {
    const referenceFile = [];

    referenceFile.push(
        <h4 key="reference-file-0-heading" style={{fontWeight: "bold"}}>Reference File</h4>,
        <Row key="row-filename">
            <span key="reference-file-0-file">{state.referenceFile.label}</span>
            <Button
                key="reference-file-0-download-button"
                className="pull-right"
                style={{marginTop: "0.5em"}} type="primary"
            >
                <Icon type="download" /> Download Reference File
            </Button>
        </Row>,
     );
     return referenceFile;
  };

  const renderSamples = () => {
    const samplesList = [];

    if(state.sequenceFilePairList.length > 0) {
        let pairIndex = 0;
        for (let i = 0; i < state.samples.length; i++) {
          samplesList.push(
            <Card
                type="inner"
                title={<a target="_blank" href={`${window.TL.BASE_URL}samples/${state.samples[i].sample.identifier}/details`}><Icon type="filter" rotate="180" /> {state.samples[i].sample.sampleName}</a>}
                extra={<a href="#">Details</a>}
                key={`sample${i}`}
            >
                <Row>
                    <a
                        target="_blank"
                        href={`${window.TL.BASE_URL}sequenceFiles/${state.samples[i].sequenceFilePair.identifier}/file/${state.sequenceFilePairList[pairIndex + 1].identifier}/summary`}>
                        <Icon type="arrow-right" /> {state.sequenceFilePairList[pairIndex].label}
                   </a>
                   <span style={{float: 'right'}}>{state.sequenceFileSizeList[pairIndex]}</span>
                </Row>
                <Row>
                    <a
                        target="_blank"
                        href={`${window.TL.BASE_URL}sequenceFiles/${state.samples[i].sequenceFilePair.identifier}/file/${state.sequenceFilePairList[pairIndex + 1].identifier}/summary`}>
                        <Icon type="arrow-left" /> {state.sequenceFilePairList[pairIndex+1].label}
                    </a>
                    <span style={{float: 'right'}}>{state.sequenceFileSizeList[pairIndex+1]}</span>
                </Row>
            </Card>
          );
           pairIndex = pairIndex + 2;
        }
    }
    else {
        samplesList.push(<p key={`no-paired-end-0`}>{getI18N("analysis.input-files.no-paired-end")}</p>);
    }
    return samplesList;
  };

  return (
    <>
      <h2 style={{ fontWeight: "bold", marginBottom: "1em" }}>{getI18N("analysis.tab.samples")}</h2>
       {
        state.referenceFile ?
            <div style={{marginBottom: "2em"}}>
                {renderReferenceFile()}
            </div>
            : null
       }

      {
        state.samples.length > 0 ?
            <div>
                {renderSamples()}
            </div>
            : null
        }
    </>
  );
}

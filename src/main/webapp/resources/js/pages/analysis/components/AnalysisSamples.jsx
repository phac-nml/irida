import React, { useContext, useEffect, useState } from "react";
import { Card, Row, Icon, Button } from "antd";
import { AnalysisDetailsContext } from '../../../state/AnalysisDetailsContext';
import { getI18N } from "../../../utilities/i18n-utilties";

import {
    getAnalysisInputFiles,
    getForwardFile,
    getReverseFile
} from "../../../apis/analysis/analysis";

export default function AnalysisSamples() {
  const { context, dispatch } = useContext(AnalysisDetailsContext);

  useEffect(() => {
    getAnalysisInputFiles(context.analysis.identifier).then(res => {

      dispatch({
         type: "SAMPLES_DATA",
         samples: res.data.samples,
         sequenceFilePairList: res.data.seqFilePairs,
         referenceFile: res.data.referenceFile,
         sequenceFileSizeList: res.data.seqFilePairSizes,
      });
    });
  }, []);

  const downloadReferenceFile = () => {
    if (context.referenceFile.identifier !== undefined)
    {
        window.open(`${window.TL.BASE_URL}referenceFiles/download/${context.referenceFile.identifier}`, '_blank');
    }
  };

  const renderReferenceFile = () => {
    const referenceFile = [];

    if(context.referenceFile.length == 0)
    {
        return null;
    }
    else {

        referenceFile.push(
            <h4 key="reference-file-0-heading" style={{fontWeight: "bold"}}>{getI18N("analysis.tab.content.samples.reference-file")}</h4>,
            <Row key="row-filename">
                <span key="reference-file-0-file">{context.referenceFile.label}</span>
                <Button
                    key="reference-file-0-download-button"
                    className="pull-right"
                    style={{marginTop: "0.5em"}} type="primary"
                    onClick={() => {downloadReferenceFile()}}
                >
                    <Icon type="download" /> {getI18N("analysis.tab.content.samples.download-reference-file")}
                </Button>
            </Row>,
         );
         return referenceFile;
     }
  };

  const renderSamples = () => {
    const samplesList = [];

    if(context.sequenceFilePairList.length > 0) {
        let pairIndex = 0;
        for (let i = 0; i < context.samples.length; i++) {
          samplesList.push(
            <Card
                type="inner"
                title={
                    <a
                        target="_blank"
                        href={`${window.TL.BASE_URL}samples/${context.samples[i].sample.identifier}/details`}>
                            <Icon type="filter" rotate="180" /> {context.samples[i].sample.sampleName}
                    </a>}
                key={`sample${i}`}
            >
                <Row>
                    <a
                        target="_blank"
                        href={`${window.TL.BASE_URL}sequenceFiles/${context.samples[i].sequenceFilePair.identifier}/file/${context.sequenceFilePairList[pairIndex].identifier}/summary`}>
                            <Icon type="arrow-right" /> {context.sequenceFilePairList[pairIndex].label}
                   </a>
                   <span style={{float: 'right'}}>{context.sequenceFileSizeList[pairIndex]}</span>
                </Row>
                <Row>
                    <a
                        target="_blank"
                        href={`${window.TL.BASE_URL}sequenceFiles/${context.samples[i].sequenceFilePair.identifier}/file/${context.sequenceFilePairList[pairIndex + 1].identifier}/summary`}>
                            <Icon type="arrow-left" /> {context.sequenceFilePairList[pairIndex+1].label}
                    </a>
                    <span style={{float: 'right'}}>{context.sequenceFileSizeList[pairIndex+1]}</span>
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
            context.referenceFile ?
                <div style={{marginBottom: "2em"}}>
                    {renderReferenceFile()}
                </div>
            : null
       }

      {
        context.samples.length > 0 ?
            <div>
                {renderSamples()}
            </div>
            : null
        }
    </>
  );
}

import React, { lazy, Suspense, useState, useReducer, useEffect } from "react";
import PropTypes from "prop-types";
import { Layout, Tabs, Col, Steps, Button, Checkbox, Input, List } from "antd";
const Step = Steps.Step;
//import analysis components required by page
import { AnalysisDetails } from "./AnalysisDetails"
import { AnalysisSteps } from "./AnalysisSteps"
import { AnalysisProvenance } from "./AnalysisProvenance"
import { AnalysisOutputFiles } from "./AnalysisOutputFiles"
import { AnalysisPhylogeneticTree } from "./AnalysisPhylogeneticTree"
import { AnalysisBioHansel } from "./AnalysisBioHansel"
import { AnalysisSistr } from "./AnalysisSistr"

import {
    updateAnalysisEmailPipelineResult,
    updateAnalysisName
} from "../../../apis/analysis/analysis";

const TabPane = Tabs.TabPane;

const data = [
  'Analysis',
  'Samples',
  'Share Results',
  'Delete Analysis',
];

const specialtyAnalysisTypes = [
    'bio_hansel Pipeline',
    'SISTR Pipeline',
    'SNVPhyl Phylogenomics Pipeline',
    'MentaLiST MLST Pipeline'
];


export default function Analysis() {
    const [analysisName, setAnalysisName] = useState(window.PAGE.analysis.name);
    const [analysisState, setAnalysisState] = useState(window.PAGE.analysis.state);
    const [stateMap, setStateMap] = useState({"NEW":0, "PREPARING":1, "SUBMITTING":2, "RUNNING":3, "COMPLETING":4, "COMPLETED":5, "ERROR":6})
    const [workflowName, setWorkflowName] = useState(window.PAGE.workflowName);

    return (
        <>
            <div style={{marginLeft: "15px", marginRight: "15px", marginTop: "15px"}}>
                <h1>{analysisName}</h1>
                <div>
                    <Steps current={stateMap[`${window.PAGE.analysisState}`]} status="finish" style={{paddingBottom: "15px"}}>
                      <Step title="Queued" />
                      <Step title="Preparing" />
                      <Step title="Submitting" />
                      <Step title="Running" />
                      <Step title="Completing" />
                      <Step title="Completed" />
                    </Steps>
                </div>
                <Tabs defaultActiveKey={
                    specialtyAnalysisTypes.indexOf(workflowName) > -1 ?
                    "0" : "1"}
                    animated={false}
                >
                    { workflowName === "bio_hansel Pipeline" ?
                        <TabPane tab="bio_hansel" key="0">
                            <AnalysisBioHansel />
                        </TabPane>
                    :
                    "" }

                    { workflowName === "SISTR Pipeline" ?
                        <TabPane tab="sistr" key="0">
                            <AnalysisSistr />
                        </TabPane>
                    :
                    "" }

                    { (workflowName === "SNVPhyl Phylogenomics Pipeline") || (workflowName == "MentaLiST MLST Pipeline") ?
                        <TabPane tab="Phylogenetic Tree" key="0">
                            <AnalysisPhylogeneticTree />
                        </TabPane>
                    :
                    "" }

                    <TabPane tab="Output Files" key="1">
                        <AnalysisOutputFiles />
                    </TabPane>

                    <TabPane tab="Provenance" key="2">
                        <AnalysisProvenance />
                    </TabPane>

                    <TabPane tab="Settings" key="3">
                        <AnalysisDetails />
                    </TabPane>
                </Tabs>
            </div>
        </>
   );
}

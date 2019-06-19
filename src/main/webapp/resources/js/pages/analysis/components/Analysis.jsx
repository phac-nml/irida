import React, { lazy, Suspense, useState, useReducer, useEffect } from "react";
import PropTypes from "prop-types";
import { Layout, Tabs, Col, Steps, Button, Checkbox, Input, List } from "antd";
const Step = Steps.Step;
//import analysis components required by page
import { AnalysisSamples } from "./AnalysisSamples"
import { AnalysisShare } from "./AnalysisShare"
import { AnalysisDelete } from "./AnalysisDelete"
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

const analysisDetails = [
  {
    title: 'ID',
    desc: `${window.PAGE.analysis.identifier}`,
  },
  {
    title: 'Pipeline',
    desc: `${window.PAGE.workflowName} (${window.PAGE.version}) `
  },
  {
    title: 'Priority',
    desc: `${window.PAGE.analysis.priority}`
  },
  {
    title: 'Created',
    desc: `${window.PAGE.analysisCreatedDate}`
  },
  {
    title: 'Duration',
    desc: '23 minutes'
  },
];

export default function Analysis() {
    const [analysisName, setAnalysisName] = useState(window.PAGE.analysis.name);
    const [analysisState, setAnalysisState] = useState(window.PAGE.analysis.state);
    const [stateMap, setStateMap] = useState({"NEW":0, "PREPARING":1, "SUBMITTING":2, "RUNNING":3, "COMPLETING":4, "COMPLETED":5, "ERROR":6})
    const [emailPipelineResult, setEmailPipelineResult] = useState(window.PAGE.analysisEmailPipelineResult);
    const [workflowName, setWorkflowName] = useState(window.PAGE.workflowName);
    const [version, setVersion] = useState(window.PAGE.version);
    const [updatePermission, setUpdatePermission] = useState(window.PAGE.updatePermission);

    function onChange(e)
    {
        updateAnalysisEmailPipelineResult(window.PAGE.analysis.identifier, e.target.checked);
    }

    function updateSubmissionName()
    {
        const updatedAnalysisName = document.getElementById("analysis-name").value.trim();
        console.log(updatedAnalysisName);
        if((updatedAnalysisName  !== "") && (updatedAnalysisName !== analysisName))
        {
            setAnalysisName(updatedAnalysisName);
            updateAnalysisName(window.PAGE.analysis.identifier, updatedAnalysisName);
        }
    }

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
                        <Tabs defaultActiveKey="4" tabPosition="left" style={{marginLeft:150, paddingTop:25}}>
                            <TabPane tab="Analysis" key="4" style={{minWidth:300}}>
                                <Col span={12}>
                                   <h2 style={{fontWeight: "bold"}}>Details</h2>
                                    <br /><br />

                                    <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                                      <label style={{fontWeight: "bold", marginBottom: "25px !important"}}>Analysis Name</label>
                                      <Input size="large" placeholder={analysisName} id="analysis-name" />
                                    </Col>
                                    <br />
                                    {updatePermission ?
                                        <Col xs={{ span: 4, offset: 1 }} lg={{ span: 4, offset: 0 }}>
                                          <Button size="large" type="primary" style={{marginTop: "5px"}} onClick={() => updateSubmissionName()}>Update</Button>
                                        </Col>
                                    :
                                        ""
                                    }

                                    <br /><br /><br /><br />
                                    <List
                                      itemLayout="horizontal"
                                      dataSource={analysisDetails}
                                      renderItem={item => (
                                        <List.Item>
                                          <List.Item.Meta
                                            title={<span style={{fontWeight: "bold"}}>{item.title}</span>}
                                            description={<span>{item.desc}</span>}
                                          />
                                        </List.Item>
                                      )}
                                    />
                                    <hr style={{backgroundColor: "#E8E8E8", height: "1px", border: "0"}} />
                                    <br />
                                    <p style={{fontWeight: "bold"}}>Receive an email upon analysis completion</p>
                                    <Checkbox onChange={onChange} defaultChecked={emailPipelineResult}>Yes, I want to receive an email once this analysis completes</Checkbox>
                                </Col>
                            </TabPane>

                            <TabPane tab="Samples" key="5">
                                <Col span={12}>
                                    <AnalysisSamples />
                                </Col>
                            </TabPane>

                            {updatePermission ?
                                <TabPane tab="Share Results" key="6">
                                    <Col span={12}>
                                        <AnalysisShare />
                                    </Col>
                                </TabPane>
                                : ""
                            }
                            <TabPane tab="Delete Analysis" key={!updatePermission ? "6" : "7"}>
                                <Col span={12}>
                                    <AnalysisDelete />
                                </Col>
                            </TabPane>
                        </Tabs>
                    </TabPane>
                </Tabs>
            </div>
        </>
   );
}

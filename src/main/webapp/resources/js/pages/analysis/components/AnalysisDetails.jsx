import React, {  useState, useEffect } from "react";
import PropTypes from "prop-types";
import { Button, Checkbox, Input, List, Col, Tabs } from "antd";
import { AnalysisSamples } from "./AnalysisSamples"
import { AnalysisShare } from "./AnalysisShare"
import { AnalysisDelete } from "./AnalysisDelete"

import {
    updateAnalysisEmailPipelineResult,
    updateAnalysisName
} from "../../../apis/analysis/analysis";

import {
  formatDate,
  getHumanizedDuration
} from "../../../utilities/date-utilities";

const TabPane = Tabs.TabPane;

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
    desc: formatDate({ date: window.PAGE.analysisCreatedDate })
  },
  {
    title: 'Duration',
    desc: getHumanizedDuration({ date: window.PAGE.duration })
  },
];

export function AnalysisDetails() {
    const [analysisName, setAnalysisName] = useState(window.PAGE.analysis.name);
    const [emailPipelineResult, setEmailPipelineResult] = useState(window.PAGE.analysisEmailPipelineResult);
    const [version, setVersion] = useState(window.PAGE.version);
    const [updatePermission, setUpdatePermission] = useState(window.PAGE.updatePermission);
    const [duration, setDuration] = useState(window.PAGE.duration);

    function onChange(e)
    {
        updateAnalysisEmailPipelineResult(window.PAGE.analysis.identifier, e.target.checked);
    }

    function updateSubmissionName()
    {
        const updatedAnalysisName = document.getElementById("analysis-name").value.trim();

        if((updatedAnalysisName  !== "") && (updatedAnalysisName !== analysisName))
        {
            setAnalysisName(updatedAnalysisName);
            updateAnalysisName(window.PAGE.analysis.identifier, updatedAnalysisName);
        }
    }

  return (
      <>
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
                          <Col xs={{ span: 4, offset: 1 }} lg={{ span: 4, offset: 1 }}>
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

              {updatePermission ?
                  <TabPane tab="Delete Analysis" key={!updatePermission ? "6" : "7"}>
                      <Col span={12}>
                          <AnalysisDelete />
                      </Col>
                  </TabPane>
                  :""
              }
          </Tabs>
      </>
  );
}

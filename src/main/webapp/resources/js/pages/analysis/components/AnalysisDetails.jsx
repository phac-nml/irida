import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { Button, Checkbox, Input, List, Col } from "antd";

import {
    getAnalysisBySubmissionId
} from "../../../apis/analysis/analysis";

const specialtyAnalysisTypes = [
    'bio_hansel',
    'sistr',
    'SNVPhyl Phylogenomics'
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

export default function AnalysisDetails() {
  const [analysis, setAnalysis] = useState({  });
    const [emailPipelineResult, setEmailPipelineResult] = useState(window.PAGE.analysisEmailPipelineResult);
    const [analysisType, setAnalysisType] = useState("bio_hansel");
    const [workflowName, setWorkflowName] = useState(window.PAGE.workflowName);
    const [version, setVersion] = useState(window.PAGE.version);
    function fetchAnalysis() {
      getAnalysisBySubmissionId(103).then( data => {
          setAnalysis(data.data);
          console.log(data.data);
      })
    }

    useEffect(() => {fetchAnalysis()},[]);

  return (
      <>
        <h2 style={{fontWeight: "bold"}}>Details</h2>
        <br /><br />

        <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
          <label style={{fontWeight: "bold", marginBottom: "25px !important"}}>Analysis Name</label>
          <Input size="large" placeholder="Analysis-Salmonella" id="analysis-name" />
        </Col>
        <br />
        <Col xs={{ span: 4, offset: 1 }} lg={{ span: 4, offset: 0 }}>
          <Button size="large" type="primary" style={{marginTop: "5px"}}>Update</Button>
        </Col>

        <br /><br /><br /><br />
        <List
          itemLayout="horizontal"
          dataSource={ldata}
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
        <Checkbox>Yes, I want to receive an email once this analysis completes</Checkbox>
      </>
  );
}

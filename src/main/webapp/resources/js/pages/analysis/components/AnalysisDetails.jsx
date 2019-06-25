import React, { useContext, Suspense, lazy } from "react";
import PropTypes from "prop-types";
import { Button, Checkbox, Input, List, Col, Row, Tabs } from "antd";
import { AnalysisContext } from '../../../state/AnalysisState';
import { getI18N } from "../../../utilities/i18n-utilties";

const AnalysisSamples = React.lazy(() => import ('./AnalysisSamples'));
const AnalysisShare = React.lazy(() => import ('./AnalysisShare'));
const AnalysisDelete = React.lazy(() => import ('./AnalysisDelete'));

import {
    updateAnalysisEmailPipelineResult,
    updateAnalysisName
} from "../../../apis/analysis/analysis";

import {
  formatDate,
  getHumanizedDuration
} from "../../../utilities/date-utilities";

const TabPane = Tabs.TabPane;

export function AnalysisDetails() {
    const { state, dispatch } = useContext(AnalysisContext);

    const analysisDetails = [
      {
        title: getI18N("analysis.tab.content.analysis.id"),
        desc: state.analysis.identifier,
      },
      {
        title: getI18N("analysis.tab.content.analysis.pipeline"),
        desc: `${state.workflowName} (${state.version})`
      },
      {
        title: getI18N("analysis.tab.content.analysis.priority"),
        desc: state.analysis.priority
      },
      {
        title: getI18N("analysis.tab.content.analysis.created"),
        desc: formatDate({ date: state.analysisCreatedDate })
      },
      {
        title: getI18N("analysis.tab.content.analysis.duration"),
        desc: getHumanizedDuration({ date: state.duration })
      },
    ];

    /*
        On change of checkbox to receive/not receive an email upon
        pipeline completion update emailPipelineResult field
    */
    function onChange(e)
    {
        updateAnalysisEmailPipelineResult(state.analysis.identifier, e.target.checked);
    }

    function updateSubmissionName()
    {
        const updatedAnalysisName = document.getElementById("analysis-name").value.trim();

        if((updatedAnalysisName  !== "") && (updatedAnalysisName !== state.analysisName))
        {
            updateAnalysisName(state.analysis.identifier, updatedAnalysisName);
            dispatch({ type: 'analysisName', analysisName: updatedAnalysisName });
        }
    }

  return (
      <>
          <Tabs defaultActiveKey="4" tabPosition="left" style={{marginLeft:150, paddingTop:25}}>
              <TabPane tab={getI18N("analysis.tab.analysis")} key="4" style={{minWidth:300}}>
                  <Col span={12}>
                    <h2 style={{fontWeight: "bold"}} className="spaced-bottom">{getI18N("analysis.tab.content.analysis.details")}</h2>

                    <Row>
                      <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                        <label style={{fontWeight: "bold"}}>{getI18N("analysis.tab.content.analysis.analysis-name")}</label>
                        <Input size="large" placeholder={state.analysisName} id="analysis-name" />
                      </Col>

                      {state.updatePermission ?
                          <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                            <Button size="large" type="primary" className="spaced-left__sm spaced-top__lg" onClick={() => updateSubmissionName()}>{getI18N("analysis.tab.content.analysis.update.button")}</Button>
                          </Col>
                        : null
                      }
                    </Row>

                      <div className="spaced-top__lg">
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
                      </div>
                      <hr style={{backgroundColor: "#E8E8E8", height: "1px", border: "0"}} />
                      <br />
                      <p style={{fontWeight: "bold"}}>{getI18N("analysis.tab.content.analysis.receive-email-upon-analysis-completion")}</p>
                      <Checkbox
                        onChange={onChange}
                        disabled={
                            ((state.isCompleted) || (state.isError)) ?
                                true : false
                        }
                        defaultChecked={state.emailPipelineResult}>
                            {getI18N("analysis.tab.content.analysis.checkbox.label")}
                      </Checkbox>
                  </Col>
              </TabPane>

              <TabPane tab={getI18N("analysis.tab.samples")} key="5">
                  <Col span={12}>
                    <Suspense fallback={<div>Loading...</div>}>
                      <AnalysisSamples />
                    </Suspense>
                  </Col>
              </TabPane>

              { state.updatePermission ?
                [
                  <TabPane tab={getI18N("analysis.tab.share-results")} key="6">
                      <Col span={12}>
                        <Suspense fallback={<div>Loading...</div>}>
                          <AnalysisShare />
                        </Suspense>
                      </Col>
                  </TabPane>,

                  <TabPane tab={getI18N("analysis.tab.delete-analysis")} key="7">
                      <Col span={12}>
                        <Suspense fallback={<div>Loading...</div>}>
                          <AnalysisDelete />
                        </Suspense>
                      </Col>
                  </TabPane>
                ]
                : null
              }
          </Tabs>
      </>
  );
}

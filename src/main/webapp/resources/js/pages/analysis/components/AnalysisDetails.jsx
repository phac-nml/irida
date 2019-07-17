import React, { useContext, Suspense, lazy, useEffect } from "react";
import PropTypes from "prop-types";
import { Button, Checkbox, Input, List, Col, Row, Tabs, Select } from "antd";
import { AnalysisContext } from '../../../state/AnalysisState';
import { getI18N } from "../../../utilities/i18n-utilties";
import { showNotification } from "../../../modules/notifications";

const AnalysisSamples = React.lazy(() => import ('./AnalysisSamples'));
const AnalysisShare = React.lazy(() => import ('./AnalysisShare'));
const AnalysisDelete = React.lazy(() => import ('./AnalysisDelete'));
const Option = Select;

import {
    updateAnalysisEmailPipelineResult,
    updateAnalysis,
    getVariablesForDetails
} from "../../../apis/analysis/analysis";

import {
  formatDate,
  getHumanizedDuration
} from "../../../utilities/date-utilities";

const TabPane = Tabs.TabPane;

export function AnalysisDetails() {
    const { state, dispatch } = useContext(AnalysisContext);

    useEffect(() => {
        //get required variables and dispatch to reducer
        getVariablesForDetails(state.analysis.identifier).then(res => {
          dispatch({
              type: 'ANALYSIS_DETAILS',
              workflowName: res.data.workflowName,
              version: res.data.version,
              priority: res.data.priority,
              analysisCreatedDate: res.data.createdDate,
              duration: res.data.duration,
              priorities: res.data.priorities,
              canShareToSamples: res.data.canShareToSamples,
              emailPipelineResult: res.data.emailPipelineResult})
        });
    }, []);

    const analysisDetails = [{
        title: getI18N("analysis.tab.content.analysis.id"),
        desc: state.analysis.identifier,
      },
      {
        title: getI18N("analysis.tab.content.analysis.pipeline"),
        desc: `${state.workflowName} (${state.version})`
      },
      {
        title: getI18N("analysis.tab.content.analysis.priority"),
        desc: state.priority
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
    function updateEmailPipelineResult(e){
        updateAnalysisEmailPipelineResult(state.analysis.identifier, e.target.checked).then(res =>
          showNotification({ text: res.message})
        );
        dispatch({ type: 'UPDATED_EMAIL_PIPELINE_RESULT', emailPipelineResult: e.target.checked })
    }

    function updateSubmissionName(){
        const updatedAnalysisName = document.getElementById("analysis-name").value.trim();

        if((updatedAnalysisName  !== "") && (updatedAnalysisName !== state.analysisName)) {
            updateAnalysis(state.analysis.identifier, updatedAnalysisName, null).then(res =>
                showNotification({ text: res.message})
            );
            dispatch({ type: 'UPDATED_ANALYSIS_NAME', analysisName: updatedAnalysisName });
        }
    }

    function updateAnalysisPriority(updatedPriority) {
        updateAnalysis(state.analysis.identifier, null, updatedPriority).then(res =>
            showNotification({ text: res.message})
        );
        dispatch({ type: 'UPDATED_PRIORITY', priority: updatedPriority });
    }

    function renderPriorities() {
        const priorityList = [];

        for(let priority of state.priorities) {
            priorityList.push(
              <Select.Option key={priority} value={priority}>{priority}</Select.Option>
            )
        }
        return priorityList;
    }

  return (
      <>
          <Tabs defaultActiveKey="4"
                tabPosition="left"
                style={{marginLeft:150, paddingTop:25}}
                animated={false}
          >
              <TabPane tab={getI18N("analysis.tab.analysis")} key="4" style={{minWidth:300}}>
                  <Col span={12}>
                    <h2 style={{fontWeight: "bold"}} className="spaced-bottom">{getI18N("analysis.tab.content.analysis.details")}</h2>

                    <Row>
                      <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                        <label style={{fontWeight: "bold"}}>{getI18N("analysis.tab.content.analysis.analysis-name")}</label>
                        <Input size="large" placeholder={state.analysis.name} id="analysis-name" />
                      </Col>

                      { state.updatePermission ?
                          <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                            <Button size="large" type="primary" className="spaced-left__sm spaced-top__lg" onClick={() => updateSubmissionName()}>{getI18N("analysis.tab.content.analysis.update.button")}</Button>
                          </Col>
                        : null
                      }
                    </Row>

                    { state.isAdmin && state.analysisState == "NEW" ?
                        <Row className="spaced-top">
                            <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                                <label style={{fontWeight: "bold"}}>Priority</label>
                                <Select defaultValue={state.analysis.priority} className="form-control" onChange={updateAnalysisPriority}>
                                  {renderPriorities()}
                                </Select>
                            </Col>
                        </Row>
                        : null
                    }

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
                      <p style={{fontWeight: "bold"}}>{getI18N("analysis.tab.content.analysis.receive-email-upon-analysis-completion")}</p>
                      <Checkbox
                        onChange={updateEmailPipelineResult}
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
                    !state.isError ?
                      <TabPane tab={getI18N("analysis.tab.share-results")} key="6">
                          <Col span={12}>
                            <Suspense fallback={<div>Loading...</div>}>
                              <AnalysisShare />
                            </Suspense>
                          </Col>
                      </TabPane>
                      : null
                     ,
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

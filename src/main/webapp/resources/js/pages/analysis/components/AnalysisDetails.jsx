import React, { useContext, Suspense, lazy, useEffect } from "react";
import PropTypes from "prop-types";
import { Button, Checkbox, Input, List, Col, Row, Tabs, Select } from "antd";
import { AnalysisContext, actions } from '../../../state/AnalysisState';
import { AnalysisDetailsContext } from '../../../state/AnalysisDetailsContext';
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
    const { context, dispatch } = useContext(AnalysisDetailsContext);

    useEffect(() => {
        //get required variables and dispatch to reducer
        getVariablesForDetails(context.analysis.identifier).then(res => {
          dispatch({
              type: 'ANALYSIS_DETAILS',
              analysisName: res.data.analysisName,
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
        desc: context.analysis.identifier,
      },
      {
        title: getI18N("analysis.tab.content.analysis.pipeline"),
        desc: `${context.workflowName} (${context.version})`
      },
      {
        title: getI18N("analysis.tab.content.analysis.priority"),
        desc: context.priority
      },
      {
        title: getI18N("analysis.tab.content.analysis.created"),
        desc: formatDate({ date: context.analysisCreatedDate })
      },
      {
        title: getI18N("analysis.tab.content.analysis.duration"),
        desc: getHumanizedDuration({ date: context.duration })
      },
    ];

    /*
        On change of checkbox to receive/not receive an email upon
        pipeline completion update emailPipelineResult field
    */
    function updateEmailPipelineResult(e){
        updateAnalysisEmailPipelineResult(context.analysis.identifier, e.target.checked).then(res => {
          showNotification({ text: res.message});
          dispatch({ type: 'UPDATED_EMAIL_PIPELINE_RESULT', emailPipelineResult: e.target.checked })
        });
    }

    function updateSubmissionName(){
        const updatedAnalysisName = document.getElementById("analysis-name").value.trim();

        if((updatedAnalysisName  !== "") && (updatedAnalysisName !== context.analysisName)) {
            updateAnalysis(context.analysis.identifier, updatedAnalysisName, null).then(res => {
                showNotification({ text: res.message});
                actions.updateSubmissionName(updatedAnalysisName);
            });
        }
    }

    function updateAnalysisPriority(updatedPriority) {
        updateAnalysis(context.analysis.identifier, null, updatedPriority).then(res => {
            showNotification({ text: res.message});
            dispatch({ type: 'UPDATED_PRIORITY', priority: updatedPriority });
        });
    }

    function renderPriorities() {
        const priorityList = [];

        for(let priority of context.priorities) {
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
                        <Input size="large" placeholder={context.analysis.name} id="analysis-name" />
                      </Col>

                      { context.updatePermission ?
                          <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                            <Button size="large" type="primary" className="spaced-left__sm spaced-top__lg" onClick={() => updateSubmissionName()}>{getI18N("analysis.tab.content.analysis.update.button")}</Button>
                          </Col>
                        : null
                      }
                    </Row>

                    { context.isAdmin && context.analysisState == "NEW" ?
                        <Row className="spaced-top">
                            <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                                <label style={{fontWeight: "bold"}}>Priority</label>
                                <Select defaultValue={context.analysis.priority} className="form-control" onChange={updateAnalysisPriority}>
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
                            ((context.isCompleted) || (context.isError)) ?
                                true : false
                        }
                        defaultChecked={context.emailPipelineResult}>
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

              { context.updatePermission ?
                [
                    !context.isError ?
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

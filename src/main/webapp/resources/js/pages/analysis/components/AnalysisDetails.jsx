import React, { useContext, Suspense, lazy, useEffect } from "react";
import { Button, Checkbox, Input, List, Col, Row, Tabs, Select } from "antd";
import { AnalysisContext } from '../../../contexts/AnalysisContext';
import { AnalysisDetailsContext } from '../../../contexts/AnalysisDetailsContext';
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
    const { analysisDetailsContext, loadAnalysisDetails } = useContext(AnalysisDetailsContext);
    const { analysisContext, analysisContextUpdateSubmissionName } = useContext(AnalysisContext);

    useEffect(() => {
        loadAnalysisDetails();
    }, []);

    const analysisDetails = [{
        title: getI18N("analysis.tab.content.analysis.id"),
        desc: analysisContext.analysis.identifier,
      },
      {
        title: getI18N("analysis.tab.content.analysis.pipeline"),
        desc: `${analysisDetailsContext.workflowName} (${analysisDetailsContext.version})`
      },
      {
        title: getI18N("analysis.tab.content.analysis.priority"),
        desc: analysisDetailsContext.priority
      },
      {
        title: getI18N("analysis.tab.content.analysis.created"),
        desc: formatDate({ date: analysisDetailsContext.createdDate })
      },
      {
        title: getI18N("analysis.tab.content.analysis.duration"),
        desc: getHumanizedDuration({ date: analysisDetailsContext.duration })
      },
    ];

    /*
        On change of checkbox to receive/not receive an email upon
        pipeline completion update emailPipelineResult field
    */
    function updateEmailPipelineResult(e){
        updateAnalysisEmailPipelineResult(analysisContext.analysis.identifier, e.target.checked).then(res => {
          showNotification({ text: res.message});
          dispatch({ type: 'UPDATED_EMAIL_PIPELINE_RESULT', emailPipelineResult: e.target.checked })
        });
    }

    // Update analysis name
    function updateSubmissionName(){
        const updatedAnalysisName = document.getElementById("analysis-name").value.trim();

        if((updatedAnalysisName  !== "") && (updatedAnalysisName !== analysisDetailsContext.analysisName)) {
            updateAnalysis(analysisContext.analysis.identifier, updatedAnalysisName, null).then(res => {
                showNotification({ text: res.message});
                analysisContextUpdateSubmissionName(updatedAnalysisName);
            });
        }
    }

    // Update analysis priority
    function updateAnalysisPriority(updatedPriority) {
        updateAnalysis(analysisContext.analysis.identifier, null, updatedPriority).then(res => {
            showNotification({ text: res.message});
            dispatch({ type: 'UPDATED_PRIORITY', priority: updatedPriority });
        });
    }

    // Render priorities in priority dropdown (Admins only)
    function renderPriorities() {
        const priorityList = [];

        for(let priority of analysisDetailsContext.priorities) {
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
                        <Input size="large" placeholder={analysisContext.analysis.name} id="analysis-name" />
                      </Col>

                      { analysisDetailsContext.updatePermission ?
                          <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                            <Button size="large" type="primary" className="spaced-left__sm spaced-top__lg" onClick={() => updateSubmissionName()}>{getI18N("analysis.tab.content.analysis.update.button")}</Button>
                          </Col>
                        : null
                      }
                    </Row>

                    { analysisContext.isAdmin && analysisContext.analysisState == "NEW" ?
                        <Row className="spaced-top">
                            <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                                <label style={{fontWeight: "bold"}}>Priority</label>
                                <Select defaultValue={analysisContext.analysis.priority} className="form-control" onChange={updateAnalysisPriority}>
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
                            ((analysisContext.isCompleted) || (analysisContext.isError)) ?
                                true : false
                        }
                        defaultChecked={analysisDetailsContext.emailPipelineResult}>
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

              { analysisDetailsContext.updatePermission ?
                [
                    !analysisContext.isError ?
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

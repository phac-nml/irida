/*
 * This file renders the details for the analysis as well as,
 * lazily loads the Samples, Share, and Delete components (component
 * is only loaded when the corresponding tab is clicked
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { useContext, Suspense, lazy, useEffect } from "react";
import {
  Button,
  Checkbox,
  Input,
  List,
  Col,
  Row,
  Tabs,
  Select,
  Spin,
  Divider,
  Typography
} from "antd";

import { AnalysisContext, isAdmin } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { showNotification } from "../../../modules/notifications";
import { SPACE_MD } from "../../../styles/spacing";

import { getVariablesForDetails } from "../../../apis/analysis/analysis";

import {
  formatDate,
  getHumanizedDuration
} from "../../../utilities/date-utilities";

const AnalysisSamples = React.lazy(() => import("./AnalysisSamples"));
const AnalysisShare = React.lazy(() => import("./AnalysisShare"));
const AnalysisDelete = React.lazy(() => import("./AnalysisDelete"));
const Option = Select;
const { Title, Paragraph } = Typography;
const TabPane = Tabs.TabPane;

export function AnalysisDetails() {
  /*
   * The following const statements
   * make the required contexts which contain
   * the state and methods available to the component
   */

  const {
    analysisDetailsContext,
    loadAnalysisDetails,
    analysisDetailsContextUpdateSubmissionPriority,
    analysisDetailsContextUpdateEmailPipelineResult
  } = useContext(AnalysisDetailsContext);

  const { analysisContext, analysisContextUpdateSubmissionName } = useContext(
    AnalysisContext
  );

  // On page load get the analysis details
  useEffect(() => {
    loadAnalysisDetails();
  }, []);

  // List of analysis details
  const analysisDetails = [
    {
      title: getI18N("analysis.tab.content.analysis.id"),
      desc: analysisContext.analysis.identifier
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
    }
  ];

  /*
        On change of checkbox to receive/not receive an email upon
        pipeline completion update emailPipelineResult field
    */
  function updateEmailPipelineResult(e) {
    analysisDetailsContextUpdateEmailPipelineResult(e.target.checked);
  }

  // Update analysis name
  function updateSubmissionName(newSubmissionName) {
    if (
      newSubmissionName !== "" &&
      newSubmissionName !== analysisContext.analysisName
    ) {
      analysisContextUpdateSubmissionName(newSubmissionName);
    }
  }

  // Update analysis priority
  function updateAnalysisPriority(updatedPriority) {
    analysisDetailsContextUpdateSubmissionPriority(updatedPriority);
  }

  // Render priorities in priority dropdown (Admins only)
  function renderPriorities() {
    const priorityList = [];

    for (let priority of analysisDetailsContext.priorities) {
      priorityList.push(
        <Select.Option key={priority} value={priority}>
          {priority}
        </Select.Option>
      );
    }
    return priorityList;
  }

  /*
   * The following renders the analysis details, and tabs
   * for Samples, Share Results, and Delete Analysis which
   * the components are only loaded if the corresponding
   * tab is clicked
   */
  return (
    <>
      <Tabs
        defaultActiveKey="analysis_details"
        tabPosition="left"
        style={{ marginLeft: 50, paddingTop: 25 }}
        animated={false}
      >
        <TabPane
          tab={getI18N("analysis.tab.content.analysis.details")}
          key="analysis_details"
          style={{ minWidth: 300 }}
        >
          <Col span={12}>
            <Title level={2}>
              {getI18N("analysis.tab.content.analysis.details")}
            </Title>

            <Row>
              <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                <label>
                  {getI18N("analysis.tab.content.analysis.analysis-name")}
                </label>

                <Paragraph editable={{ onChange: updateSubmissionName }}>
                  {analysisContext.analysisName}
                </Paragraph>
              </Col>
            </Row>

            {isAdmin && analysisContext.analysisState === "NEW" ? (
              <Row>
                <Col xs={{ span: 12, offset: 0 }} lg={{ span: 12, offset: 0 }}>
                  <label>Priority</label>
                  <Select
                    defaultValue={analysisContext.analysis.priority}
                    style={{ width: "100%" }}
                    onChange={updateAnalysisPriority}
                  >
                    {renderPriorities()}
                  </Select>
                </Col>
              </Row>
            ) : null}

            <div>
              <List
                itemLayout="horizontal"
                dataSource={analysisDetails}
                renderItem={item => (
                  <List.Item>
                    <List.Item.Meta
                      title={
                        <span style={{ fontWeight: "bold" }}>{item.title}</span>
                      }
                      description={<span>{item.desc}</span>}
                    />
                  </List.Item>
                )}
              />
            </div>

            {!analysisContext.isCompleted && !analysisContext.isError ? (
              <section style={{ marginTop: SPACE_MD }}>
                <Title level={4}>
                  {getI18N(
                    "analysis.tab.content.analysis.receive-email-upon-analysis-completion"
                  )}
                </Title>
                <Checkbox
                  onChange={updateEmailPipelineResult}
                  defaultChecked={analysisDetailsContext.emailPipelineResult}
                >
                  {getI18N("analysis.tab.content.analysis.checkbox.label")}
                </Checkbox>
              </section>
            ) : null}
          </Col>
        </TabPane>

        <TabPane tab={getI18N("analysis.tab.samples")} key="analysis_samples">
          <Col span={12}>
            <Suspense fallback={<Spin />}>
              <AnalysisSamples />
            </Suspense>
          </Col>
        </TabPane>

        {analysisDetailsContext.updatePermission
          ? [
              !analysisContext.isError ? (
                <TabPane
                  tab={getI18N("analysis.tab.share-results")}
                  key="analysis_share"
                >
                  <Col span={12}>
                    <Suspense fallback={<Spin />}>
                      <AnalysisShare />
                    </Suspense>
                  </Col>
                </TabPane>
              ) : null,
              <TabPane
                tab={getI18N("analysis.tab.delete-analysis")}
                key="analysis_delete"
              >
                <Col span={12}>
                  <Suspense fallback={<Spin />}>
                    <AnalysisDelete />
                  </Suspense>
                </Col>
              </TabPane>
            ]
          : null}
      </Tabs>
    </>
  );
}

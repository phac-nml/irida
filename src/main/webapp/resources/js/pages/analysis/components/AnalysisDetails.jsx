/*
 * This file renders the details for the analysis as well as,
 * lazily loads the Samples, Share, and Delete components (component
 * is only loaded when the corresponding tab is clicked
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { useContext, Suspense, lazy } from "react";
import {
  Button,
  Checkbox,
  Input,
  List,
  Col,
  Tabs,
  Select,
  Spin,
  Divider,
  Typography
} from "antd";

import { AnalysisContext, isAdmin } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { AnalysisSamplesProvider } from "../../../contexts/AnalysisSamplesContext";
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
    analysisDetailsContextUpdateSubmissionPriority,
    analysisDetailsContextUpdateEmailPipelineResult
  } = useContext(AnalysisDetailsContext);

  const { analysisContext, analysisContextUpdateSubmissionName } = useContext(
    AnalysisContext
  );

  // List of analysis details
  const analysisDetails = [
    {
      title: getI18N("AnalysisDetails.id"),
      desc: analysisContext.analysis.identifier
    },
    {
      title: getI18N("AnalysisDetails.pipeline"),
      desc: `${analysisDetailsContext.workflowName} (${analysisDetailsContext.version})`
    },
    {
      title: getI18N("AnalysisDetails.priority"),
      desc: analysisDetailsContext.priority
    },
    {
      title: getI18N("AnalysisDetails.created"),
      desc: formatDate({ date: analysisDetailsContext.createdDate })
    },
    {
      title: getI18N("AnalysisDetails.duration"),
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
          tab={getI18N("AnalysisDetails.details")}
          key="analysis_details"
          style={{ minWidth: 300 }}
        >
          <Col span={12}>
            <Title level={2}>{getI18N("AnalysisDetails.details")}</Title>
            <>
              <label>{getI18N("AnalysisDetails.name")}</label>
              <Paragraph editable={{ onChange: updateSubmissionName }}>
                {analysisContext.analysisName}
              </Paragraph>
            </>

            {isAdmin && analysisContext.analysisState === "NEW" ? (
              <>
                <label>Priority</label>
                <Select
                  defaultValue={analysisContext.analysis.priority}
                  style={{ width: "100%" }}
                  onChange={updateAnalysisPriority}
                >
                  {renderPriorities()}
                </Select>
              </>
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

            {window.PAGE.mailConfigured &&
            !analysisContext.isCompleted &&
            !analysisContext.isError ? (
              <section style={{ marginTop: SPACE_MD }}>
                <Title level={4}>
                  {getI18N("AnalysisDetails.receiveEmail")}
                </Title>
                <Checkbox
                  onChange={updateEmailPipelineResult}
                  checked={analysisDetailsContext.emailPipelineResult}
                >
                  {getI18N("AnalysisDetails.receiveEmailCheckboxLabel")}
                </Checkbox>
              </section>
            ) : null}
          </Col>
        </TabPane>

        <TabPane
          tab={getI18N("AnalysisSamples.samples")}
          key="analysis_samples"
        >
          <Col span={12}>
            <Suspense fallback={<Spin />}>
              <AnalysisSamplesProvider>
                <AnalysisSamples />
              </AnalysisSamplesProvider>
            </Suspense>
          </Col>
        </TabPane>

        {analysisDetailsContext.updatePermission
          ? [
              !analysisContext.isError ? (
                <TabPane
                  tab={getI18N("AnalysisShare.shareResults")}
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
                tab={getI18N("AnalysisDelete.deleteAnalysis")}
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

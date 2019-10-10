/*
 * This file renders the Analysis Details component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */
import React, { useContext } from "react";
import { Checkbox, List, Select, Typography } from "antd";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { AnalysisContext, isAdmin } from "../../../contexts/AnalysisContext";
import { SPACE_MD } from "../../../styles/spacing";

import {
  formatDate,
  getHumanizedDuration
} from "../../../utilities/date-utilities";

const { Title, Paragraph, Text } = Typography;

export default function AnalysisDetails() {
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
      title: getI18N("AnalysisDetails.name"),
      desc: (
        <Paragraph editable={{ onChange: updateSubmissionName }}>
          {analysisContext.analysisName}
        </Paragraph>
      )
    },
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
      desc:
        isAdmin && analysisContext.analysisState === "NEW"
          ? renderUpdatePrioritySection()
          : analysisDetailsContext.priority
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
          {getI18N(`AnalysisDetailsPriority.${priority}`)}
        </Select.Option>
      );
    }
    return priorityList;
  }

  function renderUpdatePrioritySection() {
    return (
      <section className="t-priority-edit">
        <Select
          defaultValue={analysisContext.analysis.priority}
          style={{ width: "100%" }}
          onChange={updateAnalysisPriority}
        >
          {renderPriorities()}
        </Select>
      </section>
    );
  }

  // The following renders the Analysis Details component view
  return (
    <>
      <Title level={2} className="t-page-title">{getI18N("AnalysisDetails.details")}</Title>
      <div>
        <List
          itemLayout="horizontal"
          dataSource={analysisDetails}
          renderItem={item => (
            <List.Item>
              <List.Item.Meta
                title={<Text strong>{item.title}</Text>}
                description={item.desc}
              />
            </List.Item>
          )}
        />
      </div>

      {window.PAGE.mailConfigured &&
      !analysisContext.isCompleted &&
      !analysisContext.isError ? (
        <section
          style={{ marginTop: SPACE_MD }}
          className="t-email-pipeline-result"
        >
          <Title level={4}>{getI18N("AnalysisDetails.receiveEmail")}</Title>
          <Checkbox
            onChange={updateEmailPipelineResult}
            checked={analysisDetailsContext.emailPipelineResult}
          >
            {getI18N("AnalysisDetails.receiveEmailCheckboxLabel")}
          </Checkbox>
        </section>
      ) : null}
    </>
  );
}

/*
 * This file renders the Analysis Details component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */
import React, { useContext } from "react";
import { Checkbox, Select, Typography } from "antd";
import { AnalysisDetailsContext } from "../../../../contexts/AnalysisDetailsContext";

import { AnalysisContext, isAdmin } from "../../../../contexts/AnalysisContext";
import { SPACE_MD } from "../../../../styles/spacing";
import { BasicList } from "../../../../components/lists/BasicList";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

import {
  formatDate,
  getHumanizedDuration
} from "../../../../utilities/date-utilities";

const { Title, Paragraph } = Typography;

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
      title: i18n("AnalysisDetails.name"),
      desc: (
        <Paragraph editable={{ onChange: updateSubmissionName }}>
          {analysisContext.analysisName}
        </Paragraph>
      )
    },
    {
      title: i18n("AnalysisDetails.description"),
      desc:
        analysisDetailsContext.analysisDescription !== ""
          ? analysisDetailsContext.analysisDescription
          : i18n("AnalysisDetails.notApplicable")
    },
    {
      title: i18n("AnalysisDetails.id"),
      desc: analysisContext.analysis.identifier
    },
    {
      title: i18n("AnalysisDetails.pipeline"),
      desc: `${analysisDetailsContext.workflowName} (${
        analysisDetailsContext.version === "unknown"
          ? i18n("AnalysisDetails.unknownVersion")
          : analysisDetailsContext.version
      })`
    },
    {
      title: i18n("AnalysisDetails.priority"),
      desc:
        isAdmin && analysisContext.analysisState === "NEW"
          ? renderUpdatePrioritySection()
          : analysisDetailsContext.priority
    },
    {
      title: i18n("AnalysisDetails.created"),
      desc: formatDate({ date: analysisDetailsContext.createdDate })
    },
    {
      title: i18n("AnalysisDetails.duration"),
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
          {i18n(`AnalysisDetailsPriority.${priority}`)}
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
    <TabPaneContent title={i18n("AnalysisDetails.details")}>
      <BasicList dataSource={analysisDetails} />

      {window.PAGE.mailConfigured &&
      !analysisContext.isCompleted &&
      !analysisContext.isError ? (
        <section
          style={{ marginTop: SPACE_MD }}
          className="t-email-pipeline-result"
        >
          <Title level={4}>{i18n("AnalysisDetails.receiveEmail")}</Title>
          <Checkbox
            onChange={updateEmailPipelineResult}
            checked={analysisDetailsContext.emailPipelineResult}
          >
            {i18n("AnalysisDetails.receiveEmailCheckboxLabel")}
          </Checkbox>
        </section>
      ) : null}
    </TabPaneContent>
  );
}

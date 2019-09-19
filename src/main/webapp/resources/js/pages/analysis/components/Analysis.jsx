/*
 * This file is responsible for displaying the
 * tabs required depending on the analysis state
 * and analysis type.
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { useContext } from "react";
import { Tabs, Typography, Icon } from "antd";

import { AnalysisDetails } from "./AnalysisDetails";
import { AnalysisSteps } from "./AnalysisSteps";
import { AnalysisProvenance } from "./AnalysisProvenance";
import { AnalysisOutputFiles } from "./AnalysisOutputFiles";
import { AnalysisPhylogeneticTree } from "./AnalysisPhylogeneticTree";
import { AnalysisBioHansel } from "./AnalysisBioHansel";
import { AnalysisSistr } from "./AnalysisSistr";
import { AnalysisError } from "./AnalysisError";
import { getI18N } from "../../../utilities/i18n-utilties";
import { SPACE_MD } from "../../../styles/spacing";

import {
  AnalysisContext,
  AnalysisProvider
} from "../../../contexts/AnalysisContext";
import { AnalysisDetailsProvider } from "../../../contexts/AnalysisDetailsContext";

const TabPane = Tabs.TabPane;
const { Title } = Typography;

// Built in Analysis Types
const analysisTypesWithAdditionalPage = [
  "BIO_HANSEL",
  "SISTR_TYPING",
  "PHYLOGENOMICS",
  "MLST_MENTALIST"
];

export default function Analysis() {
  const { analysisContext, analysisContextUpdateSubmissionName } = useContext(
    AnalysisContext
  );

  /*
   * The following renders the tabs, and selects the
   * tab depending on the state and type of analysis.
   * The steps the analysis has gone through or is
   * currently running through are only displayed
   * if the analysis has not completed. If successfully
   * completed then a green check mark is displayed next
   * to the analysis name.
   */
  return (
    <>
      <div
        style={{
          marginLeft: SPACE_MD,
          marginRight: SPACE_MD,
          marginTop: SPACE_MD
        }}
      >
        <Title>
          {analysisContext.analysisName}
          {analysisContext.analysisState === "COMPLETED" ? (
            <Icon
              type="check-circle"
              style={{ marginLeft: SPACE_MD, color: "#00ab66" }}
            />
          ) : null}
        </Title>
        {analysisContext.analysisState !== "COMPLETED" ? (
          <AnalysisSteps />
        ) : null}
        <Tabs
          defaultActiveKey={
            analysisContext.isError ||
            (analysisTypesWithAdditionalPage.indexOf(
              analysisContext.analysisType.type
            ) > -1 &&
              analysisContext.isCompleted)
              ? analysisContext.analysisType.type
              : "SETTINGS"
          }
          animated={false}
        >
          {analysisContext.isCompleted ? (
            [
              analysisContext.analysisType.type === "BIO_HANSEL" ? (
                <TabPane
                  tab="bio_hansel"
                  key="BIO_HANSEL"
                  className="t-analysis-tab-bio-hansel"
                >
                  <AnalysisBioHansel />
                </TabPane>
              ) : null,

              analysisContext.analysisType.type === "SISTR_TYPING" ? (
                <TabPane
                  tab="sistr"
                  key="SISTR_TYPING"
                  className="t-analysis-tab-sistr-typing"
                >
                  <AnalysisSistr />
                </TabPane>
              ) : null,

              analysisContext.analysisType.type === "PHYLOGENOMICS" ||
              analysisContext.analysisType.type === "MLST_MENTALIST" ? (
                <TabPane
                  tab={getI18N("Analysis.phylogeneticTree")}
                  key="PHYLOGENOMICS"
                  className="t-analysis-tab-phylogenetic"
                >
                  <AnalysisPhylogeneticTree />
                </TabPane>
              ) : null,

              <TabPane
                tab={getI18N("Analysis.outputFiles")}
                key="OUTPUT_FILES"
                className="t-analysis-tab-output-files"
              >
                <AnalysisOutputFiles />
              </TabPane>,

              <TabPane
                tab={getI18N("Analysis.provenance")}
                key="PROVENANCE"
                className="t-analysis-tab-provenance"
              >
                <AnalysisProvenance />
              </TabPane>
            ]
          ) : analysisContext.isError ? (
            <TabPane
              tab={getI18N("Analysis.jobError")}
              key="JOB_ERROR"
              className="t-analysis-tab-job-error"
            >
              <AnalysisError />
            </TabPane>
          ) : null}
          <TabPane
            tab={getI18N("Analysis.settings")}
            key="SETTINGS"
            id="t-analysis-tab-settings"
          >
            <AnalysisDetailsProvider>
              <AnalysisDetails />
            </AnalysisDetailsProvider>
          </TabPane>
        </Tabs>
      </div>
    </>
  );
}

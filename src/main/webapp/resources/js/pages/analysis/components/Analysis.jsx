/*
 * This file is responsible for displaying the
 * tabs required depending on the analysis state
 * and analysis type.
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { useContext, useState } from "react";
import { Tabs, Typography, Icon } from "antd";

import { AnalysisSettings } from "./AnalysisSettings";
import { AnalysisSteps } from "./AnalysisSteps";
import { AnalysisProvenance } from "./AnalysisProvenance";
import { AnalysisOutputFiles } from "./AnalysisOutputFiles";
import { AnalysisPhylogeneticTree } from "./AnalysisPhylogeneticTree";
import { AnalysisBioHansel } from "./AnalysisBioHansel";
import { AnalysisSistr } from "./AnalysisSistr";
import { AnalysisError } from "./AnalysisError";
import { getI18N } from "../../../utilities/i18n-utilties";
import { SPACE_MD } from "../../../styles/spacing";
import styled from "styled-components";
import { navigate } from "@reach/router";
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

const Wrapper = styled.div`
  margin-left: ${SPACE_MD};
  margin-right: ${SPACE_MD};
  margin-top: ${SPACE_MD};
`;

export default function Analysis() {
  const { analysisContext, analysisContextUpdateSubmissionName } = useContext(
    AnalysisContext
  );

  const [defaultTabKey, setDefaultTabKey] = useState(
    window.location.pathname.split("/").pop()
  );

  /*
   * Update the defaultTabKey variable with tab key that was clicked,
   * update the browser history to include the url and then switch over
   * to tab with key. Used here and for the sub-nav in the AnalysisSettings
   * component.
   */
  const updateNav = key => {
    setDefaultTabKey(key);
    window.history.pushState({ page: key }, window.location.href);
    navigate(key);
  };

  /*
   * Sets the defaultTabKey variable with tab key (either on forward
   * or back button click) and switches to the tab with key.
   */
  window.onpopstate = function(event) {
    setDefaultTabKey(document.location.href.split("/").pop());
    navigate(document.location.href.split("/").pop());
  };

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
    <Wrapper>
      <div>
        <Title style={{ wordBreak: "break-word" }}>
          {analysisContext.analysisState === "COMPLETED" ? (
            <Icon
              type="check-circle"
              style={{ marginRight: SPACE_MD, color: "#00ab66" }}
            />
          ) : null}
          {analysisContext.analysisName}
        </Title>
        {analysisContext.analysisState !== "COMPLETED" ? (
          <AnalysisSteps />
        ) : null}
        <Tabs
          activeKey={
            defaultTabKey === ""
              ? analysisContext.isError
                ? "job-error"
                : analysisTypesWithAdditionalPage.indexOf(
                    analysisContext.analysisType.type
                  ) > -1 && analysisContext.isCompleted
                ? analysisContext.analysisType.type.toLowerCase()
                : "settings"
              : defaultTabKey === "details" ||
                defaultTabKey === "samples" ||
                defaultTabKey === "share" ||
                defaultTabKey === "delete"
              ? "settings"
              : defaultTabKey
          }
          onChange={updateNav}
          animated={false}
        >
          {analysisContext.isCompleted ? (
            [
              analysisContext.analysisType.type === "BIO_HANSEL" ? (
                <TabPane
                  tab="bio_hansel"
                  key="bio_hansel"
                  className="t-analysis-tab-bio-hansel"
                >
                  <AnalysisBioHansel />
                </TabPane>
              ) : null,

              analysisContext.analysisType.type === "SISTR_TYPING" ? (
                <TabPane
                  tab="sistr"
                  key="sistr_typing"
                  className="t-analysis-tab-sistr-typing"
                >
                  <AnalysisSistr />
                </TabPane>
              ) : null,

              analysisContext.analysisType.type === "PHYLOGENOMICS" ||
              analysisContext.analysisType.type === "MLST_MENTALIST" ? (
                <TabPane
                  tab={getI18N("Analysis.phylogeneticTree")}
                  key="phylogenomics"
                  className="t-analysis-tab-phylogenetic"
                >
                  <AnalysisPhylogeneticTree />
                </TabPane>
              ) : null,

              <TabPane
                tab={getI18N("Analysis.outputFiles")}
                key="output-files"
                className="t-analysis-tab-output-files"
              >
                <AnalysisOutputFiles />
              </TabPane>,

              <TabPane
                tab={getI18N("Analysis.provenance")}
                key="provenance"
                className="t-analysis-tab-provenance"
              >
                <AnalysisProvenance />
              </TabPane>
            ]
          ) : analysisContext.isError ? (
            <TabPane
              tab={getI18N("Analysis.jobError")}
              key="job-error"
              className="t-analysis-tab-job-error"
            >
              <AnalysisError />
            </TabPane>
          ) : null}
          <TabPane
            tab={getI18N("Analysis.settings")}
            key="settings"
            id="t-analysis-tab-settings"
          >
            <AnalysisDetailsProvider>
              <AnalysisSettings
                updateNav={updateNav}
                defaultTabKey={defaultTabKey}
              />
            </AnalysisDetailsProvider>
          </TabPane>
        </Tabs>
      </div>
    </Wrapper>
  );
}

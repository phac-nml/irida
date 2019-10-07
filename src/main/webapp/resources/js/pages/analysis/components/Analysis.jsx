/*
 * This file is responsible for displaying the
 * tabs required depending on the analysis state
 * and analysis type.
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { useContext, useState, Suspense } from "react";
import { Tabs, Typography, Icon, Spin } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisSteps } from "./AnalysisSteps";
import { AnalysisSamplesProvider } from "../../../contexts/AnalysisSamplesContext";
import { AnalysisDetailsProvider } from "../../../contexts/AnalysisDetailsContext";
import { AnalysisShareProvider } from "../../../contexts/AnalysisShareContext";

import { getI18N } from "../../../utilities/i18n-utilties";
import { SPACE_MD } from "../../../styles/spacing";
import styled from "styled-components";
import { navigate } from "@reach/router";

const AnalysisBioHansel = React.lazy(() => import("./AnalysisBioHansel"));
const AnalysisError = React.lazy(() => import("./AnalysisError"));
const AnalysisOutputFiles = React.lazy(() => import("./AnalysisOutputFiles"));
const AnalysisPhylogeneticTree = React.lazy(() =>
  import("./AnalysisPhylogeneticTree")
);
const AnalysisProvenance = React.lazy(() => import("./AnalysisProvenance"));
const AnalysisSettings = React.lazy(() => import("./AnalysisSettings"));
const AnalysisSistr = React.lazy(() => import("./AnalysisSistr"));

const TabPane = Tabs.TabPane;
const { Title } = Typography;

// Built in Analysis Types
const analysisTypesWithAdditionalPage = [
  "BIO_HANSEL",
  "SISTR_TYPING",
  "PHYLOGENOMICS",
  "MLST_MENTALIST"
];

const analysisSettingsTabKeys = ["details", "samples", "share", "delete"];

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
   * Returns tab key string for the activeKey
   * parameter for Tabs.
   */
  const setActiveTabKey = () => {
    if (defaultTabKey === "") {
      if (analysisContext.isError) {
        return "job-error";
      } else {
        if (
          analysisTypesWithAdditionalPage.indexOf(
            analysisContext.analysisType.type
          ) > -1 &&
          analysisContext.isCompleted
        ) {
          return analysisContext.analysisType.type.toLowerCase();
        } else {
          return "settings";
        }
      }
    } else {
      if (analysisSettingsTabKeys.indexOf(defaultTabKey) > -1) {
        return "settings";
      } else {
        return defaultTabKey;
      }
    }
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
          activeKey={setActiveTabKey()}
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
                  <Suspense fallback={<Spin />}>
                    <AnalysisBioHansel />
                  </Suspense>
                </TabPane>
              ) : null,

              analysisContext.analysisType.type === "SISTR_TYPING" ? (
                <TabPane
                  tab="sistr"
                  key="sistr_typing"
                  className="t-analysis-tab-sistr-typing"
                >
                  <Suspense fallback={<Spin />}>
                    <AnalysisSistr />
                  </Suspense>
                </TabPane>
              ) : null,

              analysisContext.analysisType.type === "PHYLOGENOMICS" ||
              analysisContext.analysisType.type === "MLST_MENTALIST" ? (
                <TabPane
                  tab={getI18N("Analysis.phylogeneticTree")}
                  key="phylogenomics"
                  className="t-analysis-tab-phylogenetic"
                >
                  <Suspense fallback={<Spin />}>
                    <AnalysisPhylogeneticTree />
                  </Suspense>
                </TabPane>
              ) : null,

              <TabPane
                tab={getI18N("Analysis.outputFiles")}
                key="output-files"
                className="t-analysis-tab-output-files"
              >
                <Suspense fallback={<Spin />}>
                  <AnalysisOutputFiles />
                </Suspense>
              </TabPane>,

              <TabPane
                tab={getI18N("Analysis.provenance")}
                key="provenance"
                className="t-analysis-tab-provenance"
              >
                <Suspense fallback={<Spin />}>
                  <AnalysisProvenance />
                </Suspense>
              </TabPane>
            ]
          ) : analysisContext.isError ? (
            <TabPane
              tab={getI18N("Analysis.jobError")}
              key="job-error"
              className="t-analysis-tab-job-error"
            >
              <Suspense fallback={<Spin />}>
                <AnalysisError />
              </Suspense>
            </TabPane>
          ) : null}
          <TabPane
            tab={getI18N("Analysis.settings")}
            key="settings"
            id="t-analysis-tab-settings"
          >
            <Suspense fallback={<Spin />}>
              <AnalysisDetailsProvider>
                <AnalysisSamplesProvider>
                  <AnalysisShareProvider>
                    <AnalysisSettings
                      updateNav={updateNav}
                      defaultTabKey={defaultTabKey}
                    />
                  </AnalysisShareProvider>
                </AnalysisSamplesProvider>
              </AnalysisDetailsProvider>
            </Suspense>
          </TabPane>
        </Tabs>
      </div>
    </Wrapper>
  );
}

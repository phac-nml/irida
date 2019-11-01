/*
 * This file is responsible for displaying the
 * tabs required depending on the analysis state
 * and analysis type.
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { Suspense, useContext, useState } from "react";
import { Tabs } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisSteps } from "./AnalysisSteps";
import { AnalysisSamplesProvider } from "../../../contexts/AnalysisSamplesContext";
import { AnalysisDetailsProvider } from "../../../contexts/AnalysisDetailsContext";
import { AnalysisShareProvider } from "../../../contexts/AnalysisShareContext";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { getI18N } from "../../../utilities/i18n-utilties";
import { navigate } from "@reach/router";
import { ContentLoading } from "../../../components/loader/ContentLoading";

import { Error } from "../../../components/icons/Error";
import { Running } from "../../../components/icons/Running";
import { Success } from "../../../components/icons/Success";

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

const analysisSettingsTabKeys = ["details", "samples", "share", "delete"];
const analysisErrorTabKeys = [
  "job-error-info",
  "galaxy-parameters",
  "standard-error",
  "standard-out"
];
const analysisSistrTabKeys = [
  "sistr_info",
  "serovar_predictions",
  "cgmlst_330",
  "mash",
  "citation"
];

export default function Analysis() {
  const { analysisContext } = useContext(AnalysisContext);

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
    if (analysisContext.isError) {
      return "job-error";
    } else {
      if (defaultTabKey === "") {
        if (analysisContext.sistr && analysisContext.isCompleted) {
          return "sistr_typing";
        } else {
          return "settings";
        }
      } else {
        if (analysisSettingsTabKeys.indexOf(defaultTabKey) > -1) {
          return "settings";
        } else if (analysisErrorTabKeys.indexOf(defaultTabKey) > -1) {
          return "job-error";
        } else if (analysisSistrTabKeys.indexOf(defaultTabKey) > -1) {
          return "sistr_typing";
        } else {
          return defaultTabKey;
        }
      }
    }
  };

  const title = (
    <>
      {analysisContext.analysisState === "COMPLETED" ? (
        <Success />
      ) : analysisContext.analysisState === "ERROR" ? (
        <Error />
      ) : (
        <Running />
      )}
      {analysisContext.analysisName}
    </>
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
    <PageWrapper title={title}>
      {analysisContext.analysisState !== "COMPLETED" ? <AnalysisSteps /> : null}
      <Tabs activeKey={setActiveTabKey()} onChange={updateNav} animated={false}>
        {analysisContext.isCompleted ? (
          [
            analysisContext.pipelineType === "bio_hansel" ? (
              <TabPane
                tab="bio_hansel"
                key="bio_hansel"
                className="t-analysis-tab-bio-hansel"
              >
                <Suspense fallback={<ContentLoading />}>
                  <AnalysisBioHansel />
                </Suspense>
              </TabPane>
            ) : null,

            analysisContext.pipelineType === "sistr" ? (
              <TabPane
                tab="Sistr"
                key="sistr_typing"
                className="t-analysis-tab-sistr-typing"
              >
                <Suspense fallback={<ContentLoading />}>
                  <AnalysisSistr
                    updateNav={updateNav}
                    defaultTabKey={defaultTabKey}
                  />
                </Suspense>
              </TabPane>
            ) : null,

            analysisContext.pipelineType === "tree" ? (
              <TabPane
                tab={getI18N("Analysis.phylogeneticTree")}
                key="phylogenomics"
                className="t-analysis-tab-phylogenetic"
              >
                <Suspense fallback={<ContentLoading />}>
                  <AnalysisPhylogeneticTree />
                </Suspense>
              </TabPane>
            ) : null,

            <TabPane
              tab={getI18N("Analysis.outputFiles")}
              key="output-files"
              className="t-analysis-tab-output-files"
            >
              <Suspense fallback={<ContentLoading />}>
                <AnalysisOutputFiles />
              </Suspense>
            </TabPane>,

            <TabPane
              tab={getI18N("Analysis.provenance")}
              key="provenance"
              className="t-analysis-tab-provenance"
            >
              <Suspense fallback={<ContentLoading />}>
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
            <Suspense fallback={<ContentLoading />}>
              <AnalysisError
                updateNav={updateNav}
                defaultTabKey={defaultTabKey}
              />
            </Suspense>
          </TabPane>
        ) : null}
        <TabPane
          tab={getI18N("Analysis.settings")}
          key="settings"
          id="t-analysis-tab-settings"
        >
          <Suspense fallback={<ContentLoading />}>
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
    </PageWrapper>
  );
}

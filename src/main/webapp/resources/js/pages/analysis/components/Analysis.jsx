/*
 * This file is responsible for displaying the
 * tabs required depending on the analysis state
 * and analysis type.
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { lazy, Suspense, useContext } from "react";
import { Menu, Tabs } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisSteps } from "./AnalysisSteps";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { getI18N } from "../../../utilities/i18n-utilties";
import { Link, Location, Router } from "@reach/router";

import { Error } from "../../../components/icons/Error";
import { Running } from "../../../components/icons/Running";
import { Success } from "../../../components/icons/Success";
import { SPACE_MD } from "../../../styles/spacing";
import AnalysisError from "./AnalysisError";

const AnalysisBioHansel = React.lazy(() => import("./AnalysisBioHansel"));
const AnalysisPhylogeneticTree = React.lazy(() =>
  import("./AnalysisPhylogeneticTree")
);
const AnalysisSettings = React.lazy(() => import("./AnalysisSettings"));
const AnalysisSistr = React.lazy(() => import("./AnalysisSistr"));
const AnalysisSettingsContainer = lazy(() =>
  import("./settings/AnalysisSettingsContainer")
);
const AnalysisOutputFiles = lazy(() => import("./AnalysisOutputFiles"));
const AnalysisProvenance = lazy(() => import("./AnalysisProvenance"));

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
  const BASE_URL = window.PAGE.base;
  const { analysisContext } = useContext(AnalysisContext);

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

  const pathRegx = new RegExp(/\/analysis\/[0-9]+\/+([a-zA-Z]+)/);
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
      {/*<Tabs activeKey={setActiveTabKey()} onChange={updateNav} animated={false}>*/}
      {/*  {analysisContext.isCompleted ? (*/}
      {/*    [*/}
      {/*      analysisContext.analysisType === "BIO_HANSEL" ? (*/}
      {/*        <TabPane*/}
      {/*          tab="bio_hansel"*/}
      {/*          key="bio_hansel"*/}
      {/*          className="t-analysis-tab-bio-hansel"*/}
      {/*        >*/}
      {/*          <Suspense fallback={<ContentLoading />}>*/}
      {/*            <AnalysisBioHansel />*/}
      {/*          </Suspense>*/}
      {/*        </TabPane>*/}
      {/*      ) : null,*/}

      {/*      analysisContext.analysisType === "SISTR_TYPING" ? (*/}
      {/*        <TabPane*/}
      {/*          tab="Sistr"*/}
      {/*          key="sistr_typing"*/}
      {/*          className="t-analysis-tab-sistr-typing"*/}
      {/*        >*/}
      {/*          <Suspense fallback={<ContentLoading />}>*/}
      {/*            <AnalysisSistr*/}
      {/*              updateNav={updateNav}*/}
      {/*              defaultTabKey={defaultTabKey}*/}
      {/*            />*/}
      {/*          </Suspense>*/}
      {/*        </TabPane>*/}
      {/*      ) : null,*/}

      {/*      analysisContext.analysisType === "PHYLOGENOMICS" ||*/}
      {/*      analysisContext.analysisType === "MLST_MENTALIST" ? (*/}
      {/*        <TabPane*/}
      {/*          tab={getI18N("Analysis.phylogeneticTree")}*/}
      {/*          key="phylogenomics"*/}
      {/*          className="t-analysis-tab-phylogenetic"*/}
      {/*        >*/}
      {/*          <Suspense fallback={<ContentLoading />}>*/}
      {/*            <AnalysisPhylogeneticTree />*/}
      {/*          </Suspense>*/}
      {/*        </TabPane>*/}
      {/*      ) : null*/}
      {/*    ]*/}
      {/*  ) : analysisContext.isError ? (*/}
      {/*    <TabPane*/}
      {/*      tab={getI18N("Analysis.jobError")}*/}
      {/*      key="job-error"*/}
      {/*      className="t-analysis-tab-job-error"*/}
      {/*    >*/}
      {/*      <Suspense fallback={<ContentLoading />}>*/}
      {/*        <AnalysisError*/}
      {/*          updateNav={updateNav}*/}
      {/*          defaultTabKey={defaultTabKey}*/}
      {/*        />*/}
      {/*      </Suspense>*/}
      {/*    </TabPane>*/}
      {/*  ) : null}*/}
      {/*</Tabs>*/}

      <Location>
        {props => {
          const keyname = props.location.pathname.match(pathRegx);
          return (
            <Menu mode="horizontal" selectedKeys={[keyname[1] || "settings"]}>
              {analysisContext.isCompleted ? (
                <Menu.Item key="sistr">
                  <Link to={`${BASE_URL}/sistr`}>SISTR</Link>
                </Menu.Item>
              ) : null}
              {analysisContext.isError ? (
                <Menu.Item key="error">
                  <Link to={`${BASE_URL}/error`}>
                    {getI18N("Analysis.jobError")}
                  </Link>
                </Menu.Item>
              ) : (
                <Menu.Item key="output">
                  <Link to={`${BASE_URL}/output`}>
                    {getI18N("Analysis.outputFiles")}
                  </Link>
                </Menu.Item>
              )}
              <Menu.Item key="provenance">
                <Link to={`${BASE_URL}/provenance`}>
                  {getI18N("Analysis.provenance")}
                </Link>
              </Menu.Item>
              <Menu.Item key="settings">
                <Link to={`${BASE_URL}/settings/details`}>
                  {getI18N("Analysis.settings")}
                </Link>
              </Menu.Item>
            </Menu>
          );
        }}
      </Location>
      <Suspense fallback={<div>Loading ...</div>}>
        <Router style={{ paddingTop: SPACE_MD }}>
          <AnalysisError path={`${BASE_URL}/error`} />
          <AnalysisSettingsContainer path={`${BASE_URL}/settings/details`} />
          <AnalysisProvenance path={`${BASE_URL}/provenance`} />
          <AnalysisOutputFiles path={`${BASE_URL}/output`} />
        </Router>
      </Suspense>
    </PageWrapper>
  );
}

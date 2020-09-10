/*
 * This file is responsible for displaying the
 * tabs required depending on the analysis state
 * and analysis type.
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { lazy, Suspense, useContext, useEffect } from "react";
import { Menu } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisOutputsProvider } from "../../../contexts/AnalysisOutputsContext";
import { AnalysisSteps } from "./AnalysisSteps";
import { PageWrapper } from "../../../components/page/PageWrapper";

import { Link, Location, Router } from "@reach/router";

import { SPACE_MD } from "../../../styles/spacing";
import AnalysisError from "./AnalysisError";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { ANALYSIS } from "../routes";

import { setBaseUrl } from "../../../utilities/url-utilities";

import { Error, Running, Success } from "../../../components/icons";

const AnalysisBioHansel = React.lazy(() => import("./AnalysisBioHansel"));
const AnalysisPhylogeneticTree = React.lazy(() =>
  import("./AnalysisPhylogeneticTree")
);

const AnalysisSistr = React.lazy(() => import("./AnalysisSistr"));
const AnalysisSettingsContainer = lazy(() =>
  import("./settings/AnalysisSettingsContainer")
);
const AnalysisOutputFiles = lazy(() => import("./AnalysisOutputFiles"));
const AnalysisProvenance = lazy(() => import("./AnalysisProvenance"));

export default function Analysis() {
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const DEFAULT_URL = setBaseUrl(`/analysis/${analysisIdentifier}`);

  const title = (
    <>
      {analysisContext.isCompleted ? (
        <Success />
      ) : analysisContext.isError ? (
        <Error />
      ) : (
        <Running />
      )}
      {analysisContext.analysisName}
    </>
  );


  const pathRegx = new RegExp(/\/analysis\/[0-9]+\/+([a-zA-Z_0-9]+)/);

  /*
   * The following code sets the key which should
   * be highlighted by default on page load if the
   * analysis has completed. If an analysis is a type
   * of sistr, bio_hansel, phylogenomics, or mentalist,
   * then a special view is add as the default,
   * otherwise the output files tab key is set.
   * If the analysis is not completed and not errored
   * the the settings tab is the default key. If the
   * job has errored then the error tab key is set as
   * the default.
   */
  const defaultKey = analysisContext.isCompleted
    ? analysisContext.analysisViewer === "sistr"
      ? ANALYSIS.SISTR
      : analysisContext.analysisViewer === "biohansel"
      ? ANALYSIS.BIOHANSEL
      : analysisContext.analysisViewer === "tree" &&
        analysisContext.treeDefault
      ? ANALYSIS.TREE
      : ANALYSIS.OUTPUT
    : analysisContext.isError
    ? ANALYSIS.ERROR
    : ANALYSIS.SETTINGS;


  /*
   * The functions returns a set of tabs which
   * should be displayed to the user depending
   * on analysis states and types.
   */
  function getTabLinks() {
    let tabLinks = [];

    if (analysisContext.isError) {
      tabLinks.push(
        <Menu.Item key="error">
          <Link to={`${DEFAULT_URL}/${ANALYSIS.ERROR}/`}>
            {i18n("Analysis.jobError")}
          </Link>
        </Menu.Item>
      );
    } else {
      if (analysisContext.isCompleted) {
        if (analysisContext.analysisViewer === "sistr") {
          tabLinks.push(
            <Menu.Item key="sistr">
              <Link to={`${DEFAULT_URL}/${ANALYSIS.SISTR}/`}>
                {i18n("Analysis.sistr")}
              </Link>
            </Menu.Item>
          );
        } else if (analysisContext.analysisViewer === "biohansel") {
          tabLinks.push(
            <Menu.Item key="biohansel">
              <Link to={`${DEFAULT_URL}/${ANALYSIS.BIOHANSEL}`}>
                {i18n("Analysis.biohansel")}
              </Link>
            </Menu.Item>
          );
        } else if (
          analysisContext.analysisViewer === "tree"  &&
          analysisContext.treeDefault
        ) {
          tabLinks.push(
            <Menu.Item key="tree">
              <Link to={`${DEFAULT_URL}/${ANALYSIS.TREE}`}>
                {i18n("Analysis.phylogeneticTree")}
              </Link>
            </Menu.Item>
          );
        }
        tabLinks.push(
          <Menu.Item key="output">
            <Link to={`${DEFAULT_URL}/${ANALYSIS.OUTPUT}`}>
              {i18n("Analysis.outputFiles")}
            </Link>
          </Menu.Item>
        );
        tabLinks.push(
          <Menu.Item key="provenance">
            <Link to={`${DEFAULT_URL}/${ANALYSIS.PROVENANCE}`}>
              {i18n("Analysis.provenance")}
            </Link>
          </Menu.Item>
        );
      }
    }
    tabLinks.push(
      <Menu.Item key="settings">
        <Link to={`${DEFAULT_URL}/${ANALYSIS.SETTINGS}/`}>
          {i18n("Analysis.settings")}
        </Link>
      </Menu.Item>
    );

    return tabLinks;
  }

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
      {!analysisContext.isCompleted ? <AnalysisSteps /> : null}

      <Location>
        {props => {
          const keyname = props.location.pathname.match(pathRegx);

          return (
            <Menu
              mode="horizontal"
              selectedKeys={[keyname ? keyname[1] : defaultKey]}
            >
              {getTabLinks()}
            </Menu>
          );
        }}
      </Location>
      <Suspense fallback={<ContentLoading />}>
        <AnalysisOutputsProvider>
          <Router style={{ paddingTop: SPACE_MD }}>
            <AnalysisError
              path={`${DEFAULT_URL}/${ANALYSIS.ERROR}/*`}
              default={analysisContext.isError}
              key="error"
            />
            {analysisContext.isCompleted
              ? [
                  <AnalysisSistr
                    path={`${DEFAULT_URL}/${ANALYSIS.SISTR}/*`}
                    default={analysisContext.analysisViewer === "sistr"}
                    key="sistr"
                  />,
                  <AnalysisBioHansel
                    path={`${DEFAULT_URL}/${ANALYSIS.BIOHANSEL}/*`}
                    default={analysisContext.analysisViewer === "biohansel"}
                    key="biohansel"
                  />,
                  <AnalysisPhylogeneticTree
                    path={`${DEFAULT_URL}/${ANALYSIS.TREE}/*`}
                    default={
                      (analysisContext.analysisViewer === "tree") &&
                      analysisContext.treeDefault
                    }
                    key="tree"
                  />,
                  <AnalysisProvenance
                    path={`${DEFAULT_URL}/${ANALYSIS.PROVENANCE}`}
                    key="provenance"
                  />,
                  <AnalysisOutputFiles
                    path={`${DEFAULT_URL}/${ANALYSIS.OUTPUT}`}
                    default={
                      (analysisContext.analysisViewer === "none") ||
                      ((analysisContext.analysisViewer === "tree") &&
                        !analysisContext.treeDefault)
                    }
                    key="output"
                  />
                ]
              : null}
            <AnalysisSettingsContainer
              path={`${DEFAULT_URL}/${ANALYSIS.SETTINGS}/*`}
              default={!analysisContext.isError && !analysisContext.isCompleted}
              key="settings"
            />
          </Router>
        </AnalysisOutputsProvider>
      </Suspense>
    </PageWrapper>
  );
}

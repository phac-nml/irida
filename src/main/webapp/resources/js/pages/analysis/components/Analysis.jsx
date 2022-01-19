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
import { Menu, Skeleton } from "antd";
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
  const { loading, isCompleted, isError, analysisViewer, treeDefault } =
    analysisContext;

  const DEFAULT_URL = setBaseUrl(`/analysis/${analysisIdentifier}`);

  const title = (
    <>
      {isCompleted ? <Success /> : isError ? <Error /> : <Running />}
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
  const defaultKey = isCompleted
    ? analysisViewer === "sistr"
      ? ANALYSIS.SISTR
      : analysisViewer === "biohansel"
      ? ANALYSIS.BIOHANSEL
      : analysisViewer === "tree" && treeDefault
      ? ANALYSIS.TREE
      : ANALYSIS.OUTPUT
    : isError
    ? ANALYSIS.ERROR
    : ANALYSIS.SETTINGS;

  /*
   * Returns a set of tabs which
   * should be displayed to the user depending
   * on analysis states and types.
   */
  const getTabLinks = () => {
    let tabLinks = [];

    if (isError) {
      tabLinks.push(
        <Menu.Item key="error">
          <Link to={`${DEFAULT_URL}/${ANALYSIS.ERROR}/`}>
            {i18n("Analysis.jobError")}
          </Link>
        </Menu.Item>
      );
    } else {
      if (isCompleted) {
        if (analysisViewer === "sistr") {
          tabLinks.push(
            <Menu.Item key="sistr">
              <Link to={`${DEFAULT_URL}/${ANALYSIS.SISTR}/`}>
                {i18n("Analysis.sistr")}
              </Link>
            </Menu.Item>
          );
        } else if (analysisViewer === "biohansel") {
          tabLinks.push(
            <Menu.Item key="biohansel">
              <Link to={`${DEFAULT_URL}/${ANALYSIS.BIOHANSEL}`}>
                {i18n("Analysis.biohansel")}
              </Link>
            </Menu.Item>
          );
        } else if (analysisViewer === "tree" && treeDefault) {
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
  };

  /*
   * Renders the analysis steps, tabs, and the page
   * content depending on analysis state and type.
   */
  return (
    <PageWrapper title={!loading ? title : ""}>
      <Skeleton loading={loading} active>
        {!isCompleted && <AnalysisSteps key="analysis-steps" />}
        <Location key="analysis-router-location">
          {(props) => {
            const keyname = props.location.pathname.match(pathRegx);

            return (
              <Menu
                className="t-analysis-menu"
                mode="horizontal"
                selectedKeys={[keyname ? keyname[1] : defaultKey]}
              >
                {getTabLinks()}
              </Menu>
            );
          }}
        </Location>
        <Suspense fallback={<ContentLoading />} key="analysis-content-suspense">
          <AnalysisOutputsProvider>
            <Router style={{ paddingTop: SPACE_MD }}>
              <AnalysisError
                path={`${DEFAULT_URL}/${ANALYSIS.ERROR}/*`}
                default={isError}
                key="error"
              />
              {isCompleted
                ? [
                    <AnalysisSistr
                      path={`${DEFAULT_URL}/${ANALYSIS.SISTR}/*`}
                      default={analysisViewer === "sistr"}
                      key="sistr"
                    />,
                    <AnalysisBioHansel
                      path={`${DEFAULT_URL}/${ANALYSIS.BIOHANSEL}/*`}
                      default={analysisViewer === "biohansel"}
                      key="biohansel"
                    />,
                    <AnalysisPhylogeneticTree
                      path={`${DEFAULT_URL}/${ANALYSIS.TREE}/*`}
                      default={analysisViewer === "tree" && treeDefault}
                      key="tree"
                    />,
                    <AnalysisProvenance
                      path={`${DEFAULT_URL}/${ANALYSIS.PROVENANCE}`}
                      key="provenance"
                    />,
                    <AnalysisOutputFiles
                      path={`${DEFAULT_URL}/${ANALYSIS.OUTPUT}`}
                      default={
                        analysisViewer === "none" ||
                        (analysisViewer === "tree" && !treeDefault)
                      }
                      key="output"
                    />,
                  ]
                : null}
              <AnalysisSettingsContainer
                path={`${DEFAULT_URL}/${ANALYSIS.SETTINGS}/*`}
                default={!isError && !isCompleted}
                key="settings"
              />
            </Router>
          </AnalysisOutputsProvider>
        </Suspense>
      </Skeleton>
    </PageWrapper>
  );
}

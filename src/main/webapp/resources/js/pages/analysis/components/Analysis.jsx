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
import { Menu } from "antd";
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
import { ContentLoading } from "../../../components/loader/ContentLoading";

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

  const analysisType = analysisContext.analysisType;

  const pathRegx = new RegExp(/\/analysis\/[0-9]+\/+([a-zA-Z_0-9]+)/);

  const defaultKey = analysisContext.isCompleted
    ? analysisType === "SISTR_TYPING"
      ? "sistr"
      : analysisType === "BIO_HANSEL"
      ? "bio_hansel"
      : analysisType === "PHYLOGENOMICS" || analysisType === "MLST_MENTALIST"
      ? "tree"
      : null
    : analysisContext.isError
    ? "error"
    : "settings";
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

      <Location>
        {props => {
          const keyname = props.location.pathname.match(pathRegx);

          return (
            <Menu
              mode="horizontal"
              selectedKeys={[keyname ? keyname[1] : defaultKey]}
            >
              {analysisContext.isError ? (
                <Menu.Item key="error">
                  <Link to={`${BASE_URL}/error/job-error-info`}>
                    {getI18N("Analysis.jobError")}
                  </Link>
                </Menu.Item>
              ) : analysisContext.isCompleted ? (
                [
                  analysisContext.analysisType === "SISTR_TYPING" ? (
                    <Menu.Item key="sistr">
                      <Link to={`${BASE_URL}/sistr/info`}>SISTR</Link>
                    </Menu.Item>
                  ) : analysisContext.analysisType === "BIO_HANSEL" ? (
                    <Menu.Item key="bio_hansel">
                      <Link to={`${BASE_URL}/biohansel/`}>bio_hansel</Link>
                    </Menu.Item>
                  ) : (
                    <Menu.Item key="tree">
                      <Link to={`${BASE_URL}/tree/`}>Phylogenetic Tree</Link>
                    </Menu.Item>
                  ),
                  <Menu.Item key="output">
                    <Link to={`${BASE_URL}/output`}>
                      {getI18N("Analysis.outputFiles")}
                    </Link>
                  </Menu.Item>,
                  <Menu.Item key="provenance">
                    <Link to={`${BASE_URL}/provenance`}>
                      {getI18N("Analysis.provenance")}
                    </Link>
                  </Menu.Item>
                ]
              ) : null}

              <Menu.Item key="settings">
                <Link to={`${BASE_URL}/settings/details`}>
                  {getI18N("Analysis.settings")}
                </Link>
              </Menu.Item>
            </Menu>
          );
        }}
      </Location>
      <Suspense fallback={<ContentLoading />}>
        <Router style={{ paddingTop: SPACE_MD }}>
          <AnalysisError
            path={`${BASE_URL}/error/*`}
            default={analysisContext.isError}
          />
          {analysisContext.isCompleted
            ? [
                <AnalysisProvenance path={`${BASE_URL}/provenance`} />,
                <AnalysisOutputFiles path={`${BASE_URL}/output`} />
              ]
            : null}
          <AnalysisSistr
            path={`${BASE_URL}/sistr/*`}
            default={
              analysisContext.isCompleted && analysisType === "SISTR_TYPING"
            }
          />
          <AnalysisBioHansel
            path={`${BASE_URL}/biohansel/*`}
            default={
              analysisContext.isCompleted && analysisType === "BIO_HANSEL"
            }
          />
          <AnalysisPhylogeneticTree
            path={`${BASE_URL}/tree/*`}
            default={
              analysisContext.isCompleted &&
              (analysisType === "PHYLOGENOMICS" ||
                analysisType === "MLST_MENTALIST")
            }
          />
          <AnalysisSettingsContainer
            path={`${BASE_URL}/settings/*`}
            default={!analysisContext.isError && !analysisContext.isCompleted}
          />
        </Router>
      </Suspense>
    </PageWrapper>
  );
}

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
import AnalysisTitle from "./AnalysisTitle";
import { AnalysisMenu } from "../AnalysisMenu";

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

  /*
   * Renders the analysis steps, tabs, and the page
   * content depending on analysis state and type.
   */
  return (
    <PageWrapper title={<AnalysisTitle />}>
      <Skeleton loading={loading} active>
        <AnalysisSteps />
        <AnalysisMenu />
        <Suspense fallback={<ContentLoading />} key="analysis-content-suspense">
          <AnalysisOutputsProvider>
            <Router style={{ paddingTop: SPACE_MD }}>
              <AnalysisError
                path={`${DEFAULT_URL}/${ANALYSIS.ERROR}/*`}
                default={isError}
              />
              <AnalysisSistr
                path={`${DEFAULT_URL}/${ANALYSIS.SISTR}/*`}
                default={analysisViewer === "sistr"}
              />
              <AnalysisBioHansel
                path={`${DEFAULT_URL}/${ANALYSIS.BIOHANSEL}/*`}
                default={analysisViewer === "biohansel"}
              />
              <AnalysisPhylogeneticTree
                path={`${DEFAULT_URL}/${ANALYSIS.TREE}/*`}
                default={analysisViewer === "tree" && treeDefault}
              />
              <AnalysisProvenance
                path={`${DEFAULT_URL}/${ANALYSIS.PROVENANCE}`}
              />
              <AnalysisOutputFiles
                path={`${DEFAULT_URL}/${ANALYSIS.OUTPUT}`}
                default={
                  analysisViewer === "none" ||
                  (analysisViewer === "tree" && !treeDefault)
                }
              />
              <AnalysisSettingsContainer
                path={`${DEFAULT_URL}/${ANALYSIS.SETTINGS}/*`}
                default={!isError && !isCompleted}
              />
            </Router>
          </AnalysisOutputsProvider>
        </Suspense>
      </Skeleton>
    </PageWrapper>
  );
}

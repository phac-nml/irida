/*
 * This file is responsible for displaying the
 * tabs required depending on the analysis state
 * and analysis type.
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import { Skeleton, Space } from "antd";
import React, { lazy, Suspense, useContext } from "react";

import { Route, Routes } from "react-router-dom";
import { ContentLoading } from "../../components/loader/ContentLoading";
import { PageWrapper } from "../../components/page/PageWrapper";
import { AnalysisContext } from "../../contexts/AnalysisContext";
import { AnalysisOutputsProvider } from "../../contexts/AnalysisOutputsContext";

import { SPACE_MD } from "../../styles/spacing";

import { setBaseUrl } from "../../utilities/url-utilities";
import { ANALYSIS } from "./routes";
import AnalysisError from "./components/AnalysisError";
import AnalysisMenu from "./components/AnalysisMenu";
import { AnalysisSteps } from "./common/AnalysisSteps";
import AnalysisTitle from "./components/AnalysisTitle";
import AnalysisDelete from "./components/settings/AnalysisDelete";
import AnalysisDetails from "./components/settings/AnalysisDetails";
import AnalysisSamples from "./components/settings/AnalysisSamples";
import AnalysisShare from "./components/settings/AnalysisShare";
import AnalysisErrorPage from "./AnalysisErrorPage";

const AnalysisBioHansel = React.lazy(() =>
  import("./components/AnalysisBioHansel")
);
const AnalysisPhylogeneticTree = React.lazy(() =>
  import("./components/AnalysisPhylogeneticTree")
);

const AnalysisSistr = React.lazy(() => import("./components/AnalysisSistr"));
const AnalysisSettingsContainer = lazy(() =>
  import("./components/settings/AnalysisSettingsContainer")
);
const AnalysisOutputFiles = lazy(() =>
  import("./components/AnalysisOutputFiles")
);
const AnalysisProvenance = lazy(() =>
  import("./components/AnalysisProvenance")
);

export default function Analysis() {
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const { loading } = analysisContext;

  if (!loading && analysisContext.isError) {
    return <AnalysisErrorPage />;
  }

  const DEFAULT_URL = setBaseUrl(`/analysis/${analysisIdentifier}`);

  /*
   * Renders the analysis steps, tabs, and the page
   * content depending on analysis state and type.
   */
  return (
    <PageWrapper title={<AnalysisTitle />}>
      <Skeleton loading={loading} active>
        <AnalysisSteps />
        <Space direction="vertical" style={{ width: `100%` }}>
          <AnalysisMenu />
          <Suspense fallback={<ContentLoading />}>
            <AnalysisOutputsProvider>
              <Routes style={{ paddingTop: SPACE_MD }}>
                <Route
                  path={`${DEFAULT_URL}/${ANALYSIS.ERROR}/*`}
                  element={<AnalysisError />}
                />
                <Route
                  path={`${DEFAULT_URL}/${ANALYSIS.SISTR}/*`}
                  element={<AnalysisSistr />}
                />
                <Route
                  path={`${DEFAULT_URL}/${ANALYSIS.BIOHANSEL}/*`}
                  element={<AnalysisBioHansel />}
                />
                <Route
                  path={`${DEFAULT_URL}/${ANALYSIS.TREE}/*`}
                  element={<AnalysisPhylogeneticTree />}
                />
                <Route
                  path={`${DEFAULT_URL}/${ANALYSIS.PROVENANCE}`}
                  element={<AnalysisProvenance />}
                />
                <Route
                  path={`${DEFAULT_URL}/${ANALYSIS.OUTPUT}`}
                  element={<AnalysisOutputFiles />}
                />
                <Route
                  path={`${DEFAULT_URL}/${ANALYSIS.SETTINGS}/`}
                  element={<AnalysisSettingsContainer />}
                >
                  <Route path="details" element={<AnalysisDetails />} />
                  <Route path="samples" element={<AnalysisSamples />} />
                  <Route path="share" element={<AnalysisShare />} />
                  <Route path="delete" element={<AnalysisDelete />} />
                </Route>
              </Routes>
            </AnalysisOutputsProvider>
          </Suspense>
        </Space>
      </Skeleton>
    </PageWrapper>
  );
}

import React, { Suspense, useContext } from "react";
import { Provider } from "react-redux";
import { Navigate, Outlet, Route, Routes } from "react-router-dom";
import { ContentLoading } from "../../components/loader";
import { AnalysisContext } from "../../contexts/AnalysisContext";
import { AnalysisOutputsProvider } from "../../contexts/AnalysisOutputsContext";
import { SPACE_LG } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";
import AnalysisMenu from "./components/AnalysisMenu";
import { ANALYSIS } from "./routes";
import store from "./store";

const AnalysisBioHansel = React.lazy(() =>
  import("./components/AnalysisBioHansel")
);
const AnalysisOutputFiles = React.lazy(() =>
  import("./components/AnalysisOutputFiles")
);
const AnalysisAdvancedPhylo = React.lazy(() =>
  import("./components/AnalysisAdvancedPhylo")
);
const AnalysisProvenance = React.lazy(() =>
  import("./components/AnalysisProvenance")
);
const AnalysisSistr = React.lazy(() => import("./components/AnalysisSistr"));
const AnalysisDelete = React.lazy(() =>
  import("./components/settings/AnalysisDelete")
);
const AnalysisDetails = React.lazy(() =>
  import("./components/settings/AnalysisDetails")
);
const AnalysisSamples = React.lazy(() =>
  import("./components/settings/AnalysisSamples")
);
const AnalysisSettingsContainer = React.lazy(() =>
  import("./components/settings/AnalysisSettingsContainer")
);
const AnalysisShare = React.lazy(() =>
  import("./components/settings/AnalysisShare")
);

/**
 * React component to facilitate the nested routing needed for the complete
 * Analysis.
 * @returns {JSX.Element}
 */
function AnalysisOutlet() {
  return (
    <AnalysisOutputsProvider>
      <Suspense fallback={<ContentLoading />}>
        <Outlet />
      </Suspense>
    </AnalysisOutputsProvider>
  );
}

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
const getPipelineType = ({ treeDefault, analysisViewer }) =>
  analysisViewer === "sistr"
    ? ANALYSIS.SISTR
    : analysisViewer === "biohansel"
    ? ANALYSIS.BIOHANSEL
    : analysisViewer === "tree" && treeDefault
    ? ANALYSIS.TREE
    : ANALYSIS.OUTPUT;

export default function AnalysisCompletePage() {
  const { analysisContext } = useContext(AnalysisContext);
  const type = getPipelineType(analysisContext);

  const DEFAULT_URL = setBaseUrl(`/analysis/:id`);

  let component;
  let componentPath;
  if (type === "sistr") {
    component = <AnalysisSistr />;
    componentPath = `${ANALYSIS.SISTR}/*`;
  } else if (type === "biohansel") {
    component = <AnalysisBioHansel />;
    componentPath = ANALYSIS.BIOHANSEL;
  } else if (type === "tree") {
    component = (
      <Provider store={store}>
        <AnalysisAdvancedPhylo />
      </Provider>
    );
    componentPath = ANALYSIS.TREE;
  }

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "stretch",
        width: "100%",
        padding: SPACE_LG,
        gap: SPACE_LG,
      }}
    >
      <div>
        <AnalysisMenu type={type} />
      </div>
      <div style={{ flex: "1 1 auto", minHeight: "0" }}>
        <Routes>
          <Route path={DEFAULT_URL} element={<AnalysisOutlet />}>
            {type === "output" ? (
              <>
                <Route index element={<AnalysisOutputFiles />} />
                <Route
                  path={ANALYSIS.OUTPUT}
                  element={<AnalysisOutputFiles />}
                />
                <Route
                  path={ANALYSIS.PROVENANCE}
                  element={<AnalysisProvenance />}
                />
              </>
            ) : (
              <>
                <Route path={componentPath} element={component} />
                <Route index element={component} />
                <Route
                  path={ANALYSIS.PROVENANCE}
                  element={<AnalysisProvenance />}
                />
                <Route
                  path={ANALYSIS.OUTPUT}
                  element={<AnalysisOutputFiles />}
                />
              </>
            )}
            <Route
              path={ANALYSIS.SETTINGS}
              element={<AnalysisSettingsContainer />}
            >
              <Route index element={<AnalysisDetails />} />
              <Route path="samples" element={<AnalysisSamples />} />
              <Route path="share" element={<AnalysisShare />} />
              <Route path="delete" element={<AnalysisDelete />} />
              <Route path="*" element={<AnalysisDetails />} />
            </Route>
            <Route
              path="*"
              element={
                <Navigate
                  replace
                  to={setBaseUrl(
                    `analysis/${analysisContext.analysis.identifier}/`
                  )}
                />
              }
            />
          </Route>
        </Routes>
      </div>
    </div>
  );
}

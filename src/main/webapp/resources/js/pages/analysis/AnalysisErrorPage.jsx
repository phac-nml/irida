import { Menu, Space } from "antd";
import React, { Suspense, useContext } from "react";
import { Link, Route, Routes } from "react-router-dom";
import { ContentLoading } from "../../components/loader";
import { AnalysisContext } from "../../contexts/AnalysisContext";
import { SPACE_LG } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";
import { AnalysisSteps } from "./common/AnalysisSteps";
import { ANALYSIS } from "./routes";

const AnalysisError = React.lazy(() => import("./components/AnalysisError"));
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

const { Item } = Menu;

/**
 * React component to render an eerror state for an Analysis.
 * @returns {JSX.Element}
 * @constructor
 */
export default function AnalysisErrorPage() {
  const [current, setCurrent] = React.useState(() => {
    return window.location.href.includes("/settings") ? "settings" : "error";
  });
  const { analysisIdentifier } = useContext(AnalysisContext);
  const DEFAULT_URL = setBaseUrl(`/analysis/${analysisIdentifier}`);

  function handleMenu(e) {
    setCurrent(e.key);
  }

  return (
    <Space
      direction="vertical"
      size="large"
      style={{ width: `100%`, margin: SPACE_LG }}
    >
      <AnalysisSteps />
      <Menu mode="horizontal" selectedKeys={[current]} onClick={handleMenu}>
        <Item key="error">
          <Link to={`${DEFAULT_URL}/${ANALYSIS.ERROR}`}>
            {i18n("Analysis.jobError")}
          </Link>
        </Item>
        <Item key="settings">
          <Link to={`${DEFAULT_URL}/${ANALYSIS.SETTINGS}`}>
            {i18n("Analysis.settings")}
          </Link>
        </Item>
      </Menu>
      <Suspense fallback={<ContentLoading />}>
        <Routes>
          <Route path={DEFAULT_URL} element={<AnalysisError />} />
          <Route
            path={`${DEFAULT_URL}/${ANALYSIS.ERROR}/*`}
            element={<AnalysisError />}
          />
          <Route
            path={`${DEFAULT_URL}/${ANALYSIS.SETTINGS}/`}
            element={<AnalysisSettingsContainer />}
          >
            <Route index element={<AnalysisDetails />} />
            <Route path="samples" element={<AnalysisSamples />} />
            <Route path="delete" element={<AnalysisDelete />} />
            <Route path="*" element={<AnalysisDetails />} />
          </Route>
          <Route path={`${DEFAULT_URL}/*`} element={<AnalysisError />} />
        </Routes>
      </Suspense>
    </Space>
  );
}

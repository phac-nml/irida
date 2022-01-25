import { Layout, Menu, Space } from "antd";
import React, { useContext } from "react";
import { Link, Route, Routes } from "react-router-dom";
import { IconLoading } from "../../components/icons/Icons";
import { AnalysisContext } from "../../contexts/AnalysisContext";
import { blue6, grey1 } from "../../styles/colors";
import { SPACE_LG } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";
import { AnalysisSteps } from "./common/AnalysisSteps";

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

const { Item } = Menu;

/**
 * React component to render the status of an analysis that is running
 * @returns {JSX.Element}
 * @constructor
 */
export default function AnalysisRunningPage() {
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const { analysisName } = analysisContext;
  const DEFAULT_URL = setBaseUrl(`/analysis/${analysisIdentifier}`);

  return (
    <Layout style={{ height: `100%` }}>
      <Layout.Content
        style={{
          margin: 24,
          backgroundColor: grey1,
        }}
      >
        <Space
          direction="vertical"
          size="large"
          style={{ margin: SPACE_LG, display: "flex" }}
        >
          <AnalysisSteps />
          <Menu mode="horizontal" selectedKeys={["settings"]}>
            <Item key="settings">
              <Link to={`${DEFAULT_URL}`}>{i18n("Analysis.settings")}</Link>
            </Item>
          </Menu>
          <Routes>
            <Route
              path={`${DEFAULT_URL}/`}
              element={<AnalysisSettingsContainer />}
            >
              <Route index element={<AnalysisDetails />} />
              <Route path="samples" element={<AnalysisSamples />} />
              <Route path="share" element={<AnalysisShare />} />
              <Route path="delete" element={<AnalysisDelete />} />
            </Route>
          </Routes>
        </Space>
      </Layout.Content>
    </Layout>
  );
}

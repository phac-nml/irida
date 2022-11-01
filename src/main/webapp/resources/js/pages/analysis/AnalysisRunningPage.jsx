import { Layout, MenuProps, Space } from "antd";
import React, { useContext } from "react";
import { Route, Routes, useNavigate } from "react-router-dom";
import { AnalysisContext } from "../../contexts/AnalysisContext";
import { grey1 } from "../../styles/colors";
import { SPACE_LG } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";
import { AnalysisSteps } from "./common/AnalysisSteps";
import HorizontalMenu from "../../components/ant.design/HorizontalMenu";

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
 * React component to render the status of an analysis that is running
 * @returns {JSX.Element}
 * @constructor
 */
export default function AnalysisRunningPage() {
  const navigate = useNavigate();
  const { analysisIdentifier } = useContext(AnalysisContext);
  const DEFAULT_URL = setBaseUrl(`/analysis/${analysisIdentifier}`);

  const updateMenu: MenuProps["click"] = ({ key }) => {
    if (key === "analysis:setting") {
      navigate(`${DEFAULT_URL}`);
    }
  };

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
          <HorizontalMenu
            items={[
              { key: "analysis:setting", label: i18n("Analysis.settings") },
            ]}
            mode="horizontal"
            selectedKeys={["settings"]}
            onClick={updateMenu}
          />
          <Routes>
            <Route
              path={`${DEFAULT_URL}/`}
              element={<AnalysisSettingsContainer />}
            >
              <Route index element={<AnalysisDetails />} />
              <Route path="samples" element={<AnalysisSamples />} />
              <Route path="share" element={<AnalysisShare />} />
              <Route path="delete" element={<AnalysisDelete />} />
              <Route path="*" element={<AnalysisDetails />} />
            </Route>
          </Routes>
        </Space>
      </Layout.Content>
    </Layout>
  );
}

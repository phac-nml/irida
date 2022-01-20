import React, { useContext } from "react";
import { Link, Routes, Route } from "react-router-dom";
import { Layout, Menu, PageHeader, Space } from "antd";
import { AnalysisContext } from "../../contexts/AnalysisContext";
import { IconLoading } from "../../components/icons/Icons";
import { blue6, grey1 } from "../../styles/colors";
import { AnalysisSteps } from "./common/AnalysisSteps";
import { ANALYSIS } from "./routes";
import { setBaseUrl } from "../../utilities/url-utilities";
import { SPACE_LG } from "../../styles/spacing";
import AnalysisError from "./components/AnalysisError";
import AnalysisSettingsContainer from "./components/settings/AnalysisSettingsContainer";
import AnalysisDetails from "./components/settings/AnalysisDetails";
import AnalysisSamples from "./components/settings/AnalysisSamples";
import AnalysisShare from "./components/settings/AnalysisShare";
import AnalysisDelete from "./components/settings/AnalysisDelete";

const { Item } = Menu;

export default function AnalysisErrorPage() {
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const { analysis, isLoading, isCompleted, isError, analysisName } =
    analysisContext;
  const DEFAULT_URL = setBaseUrl(`/analysis/${analysis.identifier}`);

  function handleMenu(e) {
    setCurrent(e.key);
  }

  return (
    <Layout style={{ height: `100%` }}>
      <Layout.Content
        style={{
          margin: 24,
          backgroundColor: grey1,
        }}
      >
        <PageHeader
          title={analysisName}
          avatar={{ style: { backgroundColor: blue6 }, icon: <IconLoading /> }}
        />
        <Space
          direction="vertical"
          size="large"
          style={{ margin: SPACE_LG, display: "flex" }}
        >
          <AnalysisSteps />
          <Menu
            mode="horizontal"
            selectedKeys={["settings"]}
            onClick={handleMenu}
          >
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

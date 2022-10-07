/*
 * This file renders the details for the analysis as well as,
 * lazily loads the Samples, Share, and Delete components (component
 * is only loaded when the corresponding tab is clicked
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { Suspense, useContext } from "react";
import { Layout, Menu } from "antd";
import { Link, useLocation, Outlet } from "react-router-dom";

import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";

import { SPACE_MD } from "../../../styles/spacing";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { grey1 } from "../../../styles/colors";
import { ANALYSIS, SETTINGS } from "../routes";
import { setBaseUrl } from "../../../utilities/url-utilities";
import ScrollableSection from "./phylocanvas/ScrollableSection";

const { Content, Sider } = Layout;

// Regex for path where the url has settings in it
const settingsPathRegx = /\/settings\/(?<path>\w+)/;
// Regex for path where the url doesn't have settings in it
const nonSettingsPathRegx = /\/\d+\/(?<path>\w+)/;

export default function AnalysisSettings() {
  const location = useLocation();
  const [current, setCurrent] = React.useState();
  const { analysisDetailsContext } = useContext(AnalysisDetailsContext);
  const { analysisContext } = useContext(AnalysisContext);

  const analysisRunning =
    !analysisContext.isError && !analysisContext.isCompleted;

  React.useEffect(() => {
    const found =
      location.pathname.match(settingsPathRegx) ||
      location.pathname.match(nonSettingsPathRegx);
    const path = found ? found.groups.path : SETTINGS.DETAILS;

    if (path === "settings") {
      setCurrent(SETTINGS.DETAILS);
    } else {
      setCurrent(path);
    }
  }, [location.pathname]);

  /*
   * The following renders the analysis details, and tabs
   * for Samples, Share Results, and Delete Analysis which
   * the components are only loaded if the corresponding
   * tab is clicked
   */
  return (
    <Layout style={{ height: `100%` }}>
      <Sider width={200} style={{ backgroundColor: grey1 }}>
        <Menu mode="vertical" selectedKeys={[current]}>
          <Menu.Item key="details">
            <Link to={""}>{i18n("AnalysisDetails.details")}</Link>
          </Menu.Item>
          <Menu.Item key="samples">
            <Link to={SETTINGS.SAMPLES}>{i18n("AnalysisSamples.samples")}</Link>
          </Menu.Item>
          {analysisDetailsContext.updatePermission
            ? [
                analysisContext.isError ? null : (
                  <Menu.Item key="share">
                    <Link to={SETTINGS.SHARE}>
                      {i18n("AnalysisShare.manageResults")}
                    </Link>
                  </Menu.Item>
                ),
                <Menu.Item key="delete">
                  <Link to={SETTINGS.DELETE}>
                    {i18n("AnalysisDelete.deleteAnalysis")}
                  </Link>
                </Menu.Item>,
              ]
            : null}
        </Menu>
      </Sider>

      <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
        <Content>
          <ScrollableSection>
            <Suspense fallback={<ContentLoading />}>
              <Outlet />
            </Suspense>
          </ScrollableSection>
        </Content>
      </Layout>
    </Layout>
  );
}

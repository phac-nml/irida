/*
 * This file renders the details for the analysis as well as,
 * lazily loads the Samples, Share, and Delete components (component
 * is only loaded when the corresponding tab is clicked
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { Suspense, useContext, useMemo } from "react";
import type { MenuProps } from "antd";
import { Layout, Menu } from "antd";
import { Outlet, useLocation, useNavigate } from "react-router-dom";

import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";

import { SPACE_MD } from "../../../styles/spacing";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { grey1 } from "../../../styles/colors";
import ScrollableSection from "./ScrollableSection";

const { Content, Sider } = Layout;

// Regex for path where the url has settings in it
const settingsPathRegx = /\/settings\/(?<path>\w+)/;
// Regex for path where the url doesn't have settings in it
const nonSettingsPathRegx = /\/\d+\/(?<path>\w+)/;

export default function AnalysisSettings(): JSX.Element {
  const location = useLocation();
  const navigate = useNavigate();
  const [current, setCurrent] = React.useState<string>(() => {
    const found =
      location.pathname.match(settingsPathRegx) ||
      location.pathname.match(nonSettingsPathRegx);

    if (found && found.groups && found.groups.path !== "settings") {
      return `analysis:settings:${found.groups.path}`;
    } else {
      return "analysis:settings:details";
    }
  });

  const { analysisDetailsContext } = useContext(AnalysisDetailsContext);
  const { analysisContext } = useContext(AnalysisContext);

  const analysisRunning =
    !analysisContext.isError && !analysisContext.isCompleted;

  const menuItems: MenuProps["items"] = useMemo(() => {
    const items: MenuProps["items"] = [
      {
        key: "analysis:settings:details",
        label: i18n("AnalysisDetails.details"),
      },
      {
        key: "analysis:settings:samples",
        label: i18n("AnalysisSamples.samples"),
      },
    ];

    if (analysisDetailsContext.updatePermission && !analysisContext.isError) {
      items.push({
        key: "analysis:settings:share",
        label: i18n("AnalysisShare.manageResults"),
      });
    }

    if (analysisDetailsContext.updatePermission) {
      items.push({
        key: "analysis:settings:delete",
        label: i18n("AnalysisDelete.deleteAnalysis"),
      });
    }

    return items;
  }, [analysisContext.isError, analysisDetailsContext.updatePermission]);

  const onClick: MenuProps["onClick"] = ({ key }) => {
    setCurrent(key);
    navigate(key.split(":")[2]);
  };

  /*
   * The following renders the analysis details, and tabs
   * for Samples, Share Results, and Delete Analysis which
   * the components are only loaded if the corresponding
   * tab is clicked
   */
  return (
    <Layout style={{ height: `100%` }}>
      <Sider width={200} style={{ backgroundColor: grey1 }}>
        <Menu
          items={menuItems}
          mode="vertical"
          onClick={onClick}
          selectedKeys={[current]}
        />
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

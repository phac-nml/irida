/*
 * This file renders the details for the analysis as well as,
 * lazily loads the Samples, Share, and Delete components (component
 * is only loaded when the corresponding tab is clicked
 */

/*
 * The following import statements makes available all the elements
 *required by the components encompassed within
 */

import React, { lazy, Suspense, useContext } from "react";
import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";

import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { SPACE_MD } from "../../../styles/spacing";

const AnalysisDetails = lazy(() => import("./settings/AnalysisDetails"));
const AnalysisSamples = lazy(() => import("./settings/AnalysisSamples"));

const AnalysisShare = React.lazy(() => import("./settings/AnalysisShare"));
const AnalysisDelete = React.lazy(() => import("./settings/AnalysisDelete"));

const { Content, Sider } = Layout;

export default function AnalysisSettings(props) {
  const { analysisDetailsContext } = useContext(AnalysisDetailsContext);
  const { analysisContext } = useContext(AnalysisContext);

  const BASE_URL = window.PAGE.base;
  const pathRegx = new RegExp(/([a-zA-Z]+)$/);
  /*
   * The following renders the analysis details, and tabs
   * for Samples, Share Results, and Delete Analysis which
   * the components are only loaded if the corresponding
   * tab is clicked
   */
  return (
    <Layout>
      <Sider width={200} style={{ background: "#fff" }}>
        <Location>
          {props => {
            const keyname = props.location.pathname.match(pathRegx);

            return (
              <Menu
                mode="vertical"
                selectedKeys={[keyname ? keyname[1] : "details"]}
              >
                <Menu.Item key="details">
                  <Link to={`${BASE_URL}/details`}>
                    {getI18N("AnalysisDetails.details")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="samples">
                  <Link to={`${BASE_URL}/samples`}>
                    {getI18N("AnalysisSamples.samples")}
                  </Link>
                </Menu.Item>
                {analysisDetailsContext.updatePermission
                  ? [
                      analysisContext.isError ? null : (
                        <Menu.Item key="share">
                          <Link to={`${BASE_URL}/share`}>
                            {getI18N("AnalysisShare.manageResults")}
                          </Link>
                        </Menu.Item>
                      ),
                      <Menu.Item key="delete">
                        <Link to={`${BASE_URL}/delete`}>
                          {getI18N("AnalysisDelete.deleteAnalysis")}
                        </Link>
                      </Menu.Item>
                    ]
                  : null}
              </Menu>
            );
          }}
        </Location>
      </Sider>

      <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: "white" }}>
        <Content>
          <Suspense fallback={<div>Loading ...</div>}>
            <Router>
              <AnalysisDetails path={`${BASE_URL}/details`} default />
              <AnalysisSamples path={`${BASE_URL}/samples`} />
              <AnalysisShare path={`${BASE_URL}/share`} />
              <AnalysisDelete path={`${BASE_URL}/delete`} />
            </Router>
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  );
}

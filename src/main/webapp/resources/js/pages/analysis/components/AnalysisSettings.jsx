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
import { getI18N } from "../../../utilities/i18n-utilities";
import { SPACE_MD } from "../../../styles/spacing";
import { ContentLoading } from "../../../components/loader/ContentLoading";

const AnalysisDetails = lazy(() => import("./settings/AnalysisDetails"));
const AnalysisSamples = lazy(() => import("./settings/AnalysisSamples"));

const AnalysisShare = React.lazy(() => import("./settings/AnalysisShare"));
const AnalysisDelete = React.lazy(() => import("./settings/AnalysisDelete"));

const { Content, Sider } = Layout;

export default function AnalysisSettings() {
  const { analysisDetailsContext } = useContext(AnalysisDetailsContext);
  const { analysisContext } = useContext(AnalysisContext);

  const BASE_URL = `${window.PAGE.base}/settings`;
  const pathRegx = new RegExp(/([a-zA-Z]+)$/);
  const analysisRunning =
    !analysisContext.isError && !analysisContext.isCompleted;

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
          <Suspense fallback={<ContentLoading />}>
            <Router>
              <AnalysisDetails
                path={analysisRunning ? `${BASE_URL}/details` : "details"}
                default
              />
              <AnalysisSamples
                path={analysisRunning ? `${BASE_URL}/samples` : "samples"}
              />
              {!analysisContext.isError ? (
                <AnalysisShare
                  path={analysisRunning ? `${BASE_URL}/share` : "share"}
                />
              ) : null}

              <AnalysisDelete
                path={analysisRunning ? `${BASE_URL}/delete` : "delete"}
              />
            </Router>
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  );
}

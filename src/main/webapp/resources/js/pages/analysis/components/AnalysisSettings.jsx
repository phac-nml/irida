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

import { SPACE_MD } from "../../../styles/spacing";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { grey1 } from "../../../styles/colors";
import { ANALYSIS, SETTINGS } from "../routes";
import { setBaseUrl } from "../../../utilities/url-utilities";

const AnalysisDetails = lazy(() => import("./settings/AnalysisDetails"));
const AnalysisSamples = lazy(() => import("./settings/AnalysisSamples"));

const AnalysisShare = React.lazy(() => import("./settings/AnalysisShare"));
const AnalysisDelete = React.lazy(() => import("./settings/AnalysisDelete"));

const { Content, Sider } = Layout;

export default function AnalysisSettings() {
  const { analysisDetailsContext } = useContext(AnalysisDetailsContext);
  const { analysisContext } = useContext(AnalysisContext);

  const DEFAULT_URL =
    setBaseUrl(`/analysis/${analysisContext.analysis.identifier}/` + ANALYSIS.SETTINGS);

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
      <Sider width={200} style={{ backgroundColor: grey1 }}>
        <Location>
          {props => {
            const keyname = props.location.pathname.match(pathRegx);
            return (
              <Menu
                mode="vertical"
                selectedKeys={[keyname ? keyname[1] : SETTINGS.DETAILS]}
              >
                <Menu.Item key="details">
                  <Link to={`${DEFAULT_URL}/${SETTINGS.DETAILS}`}>
                    {i18n("AnalysisDetails.details")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="samples">
                  <Link to={`${DEFAULT_URL}/${SETTINGS.SAMPLES}`}>
                    {i18n("AnalysisSamples.samples")}
                  </Link>
                </Menu.Item>
                {analysisDetailsContext.updatePermission
                  ? [
                      analysisContext.isError ? null : (
                        <Menu.Item key="share">
                          <Link to={`${DEFAULT_URL}/${SETTINGS.SHARE}`}>
                            {i18n("AnalysisShare.manageResults")}
                          </Link>
                        </Menu.Item>
                      ),
                      <Menu.Item key="delete">
                        <Link to={`${DEFAULT_URL}/${SETTINGS.DELETE}`}>
                          {i18n("AnalysisDelete.deleteAnalysis")}
                        </Link>
                      </Menu.Item>
                    ]
                  : null}
              </Menu>
            );
          }}
        </Location>
      </Sider>

      <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
        <Content>
          <Suspense fallback={<ContentLoading />}>
            <Router>
              <AnalysisDetails
                path={
                  analysisRunning
                    ? `${DEFAULT_URL}/${SETTINGS.DETAILS}`
                    : SETTINGS.DETAILS
                }
                default
              />
              <AnalysisSamples
                path={
                  analysisRunning
                    ? `${DEFAULT_URL}/${SETTINGS.SAMPLES}`
                    : SETTINGS.SAMPLES
                }
              />
              {!analysisContext.isError ? (
                <AnalysisShare
                  path={
                    analysisRunning
                      ? `${DEFAULT_URL}/${SETTINGS.SHARE}`
                      : SETTINGS.SHARE
                  }
                />
              ) : null}

              <AnalysisDelete
                path={
                  analysisRunning
                    ? `${DEFAULT_URL}/${SETTINGS.DELETE}`
                    : SETTINGS.DELETE
                }
              />
            </Router>
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  );
}

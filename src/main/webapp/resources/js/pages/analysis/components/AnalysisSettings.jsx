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
import { Col, Menu, Row, Tabs } from "antd";
import { Link, Location, Router } from "@reach/router";

import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { SPACE_MD } from "../../../styles/spacing";

const AnalysisDetails = lazy(() => import("./settings/AnalysisDetails"));
const AnalysisSamples = lazy(() => import("./settings/AnalysisSamples"));

const AnalysisShare = React.lazy(() => import("./settings/AnalysisShare"));
const AnalysisDelete = React.lazy(() => import("./settings/AnalysisDelete"));
const TabPane = Tabs.TabPane;

export default function AnalysisSettings(props) {
  const { analysisDetailsContext } = useContext(AnalysisDetailsContext);
  const { analysisContext } = useContext(AnalysisContext);

  const pathRegx = new RegExp(/(\w+)$/);
  /*
   * The following renders the analysis details, and tabs
   * for Samples, Share Results, and Delete Analysis which
   * the components are only loaded if the corresponding
   * tab is clicked
   */
  return (
    <Row>
      <Col span={4}>
        <Location>
          {props => {
            const keyname = props.location.pathname.match(pathRegx);
            return (
              <Menu mode="vertical" selectedKeys={[keyname[1] || "details"]}>
                <Menu.Item key="details">
                  <Link to="details">{getI18N("AnalysisDetails.details")}</Link>
                </Menu.Item>
                <Menu.Item key="samples">
                  <Link to="samples">{getI18N("AnalysisSamples.samples")}</Link>
                </Menu.Item>
                {analysisDetailsContext.updatePermission
                  ? [
                      analysisContext.isError ? null : (
                        <Menu.Item key="share">
                          <Link to="share">
                            {getI18N("AnalysisShare.manageResults")}
                          </Link>
                        </Menu.Item>
                      ),
                      <Menu.Item key="delete">
                        <Link to="delete">
                          {getI18N("AnalysisDelete.deleteAnalysis")}
                        </Link>
                      </Menu.Item>
                    ]
                  : null}
              </Menu>
            );
          }}
        </Location>
      </Col>

      <Col span={12} style={{ paddingLeft: SPACE_MD }}>
        <Suspense fallback={<div>Loading ...</div>}>
          <Router>
            <AnalysisDetails path="details" />
            <AnalysisSamples path="samples" />
            <AnalysisShare key="share" path="share" />,
            <AnalysisDelete key="delete" path="delete" />
          </Router>
        </Suspense>
      </Col>

      {/*  {analysisDetailsContext.updatePermission*/}
      {/*    ? [*/}
      {/*        !analysisContext.isError ? (*/}
      {/*          <TabPane*/}
      {/*            tab={getI18N("AnalysisShare.manageResults")}*/}
      {/*            key="share"*/}
      {/*            className="t-analysis-settings-tab-share-results"*/}
      {/*          >*/}
      {/*            <Col span={12}>*/}
      {/*              <Suspense fallback={<ContentLoading />}>*/}
      {/*                <AnalysisShare />*/}
      {/*              </Suspense>*/}
      {/*            </Col>*/}
      {/*          </TabPane>*/}
      {/*        ) : null,*/}
      {/*        <TabPane*/}
      {/*          tab={getI18N("AnalysisDelete.deleteAnalysis")}*/}
      {/*          key="delete"*/}
      {/*          className="t-analysis-settings-tab-delete-analysis"*/}
      {/*        >*/}
      {/*          <Col span={12}>*/}
      {/*            <Suspense fallback={<ContentLoading />}>*/}
      {/*              <AnalysisDelete />*/}
      {/*            </Suspense>*/}
      {/*          </Col>*/}
      {/*        </TabPane>*/}
      {/*      ]*/}
      {/*    : null}*/}
      {/*</SideTabs>*/}
    </Row>
  );
}

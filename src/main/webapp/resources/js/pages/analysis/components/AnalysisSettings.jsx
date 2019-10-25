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
import { Col, Tabs } from "antd";

import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { SideTabs } from "../../../components/tabs/SideTabs";
import { ContentLoading } from "../../../components/loader/ContentLoading";

const AnalysisDetails = React.lazy(() => import("./settings/AnalysisDetails"));
const AnalysisSamples = React.lazy(() => import("./settings/AnalysisSamples"));
const AnalysisShare = React.lazy(() => import("./settings/AnalysisShare"));
const AnalysisDelete = React.lazy(() => import("./settings/AnalysisDelete"));
const TabPane = Tabs.TabPane;

export default function AnalysisSettings(props) {
  const { analysisDetailsContext } = useContext(AnalysisDetailsContext);
  const { analysisContext } = useContext(AnalysisContext);

  /*
   * The following renders the analysis details, and tabs
   * for Samples, Share Results, and Delete Analysis which
   * the components are only loaded if the corresponding
   * tab is clicked
   */
  return (
    <SideTabs
      activeKey={
        props.defaultTabKey === "" || props.defaultTabKey === "settings"
          ? "details"
          : props.defaultTabKey
      }
      onChange={props.updateNav}
    >
      <TabPane
        tab={getI18N("AnalysisDetails.details")}
        key="details"
        className="t-analysis-settings-tab-details"
      >
        <Col span={12}>
          <Suspense fallback={<ContentLoading />}>
            <AnalysisDetails />
          </Suspense>
        </Col>
      </TabPane>

      <TabPane
        tab={getI18N("AnalysisSamples.samples")}
        key="samples"
        className="t-analysis-settings-tab-samples"
      >
        <Col span={12}>
          <Suspense fallback={<ContentLoading />}>
            <AnalysisSamples />
          </Suspense>
        </Col>
      </TabPane>

      {analysisDetailsContext.updatePermission
        ? [
            !analysisContext.isError ? (
              <TabPane
                tab={getI18N("AnalysisShare.manageResults")}
                key="share"
                className="t-analysis-settings-tab-share-results"
              >
                <Col span={12}>
                  <Suspense fallback={<ContentLoading />}>
                    <AnalysisShare />
                  </Suspense>
                </Col>
              </TabPane>
            ) : null,
            <TabPane
              tab={getI18N("AnalysisDelete.deleteAnalysis")}
              key="delete"
              className="t-analysis-settings-tab-delete-analysis"
            >
              <Col span={12}>
                <Suspense fallback={<ContentLoading />}>
                  <AnalysisDelete />
                </Suspense>
              </Col>
            </TabPane>
          ]
        : null}
    </SideTabs>
  );
}

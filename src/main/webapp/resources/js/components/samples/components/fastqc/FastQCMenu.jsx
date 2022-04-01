import React, { Suspense } from "react";
import { Tabs } from "antd";
import { ContentLoading } from "../../../loader";

const FastQCCharts = React.lazy(() => import("./FastQCCharts"));
const FastQCDetails = React.lazy(() => import("./FastQCDetails"));
const OverRepresentedSequences = React.lazy(() =>
  import("./OverRepresentedSequences")
);

/**
 * React component to render tab menu for the FastQC results
 * @returns {JSX.Element}
 * @constructor
 */
export function FastQCMenu() {
  return (
    <>
      <Tabs defaultActiveKey="charts">
        <Tabs.TabPane tab={i18n("FastQC.charts")} key="charts">
          <Suspense fallback={<ContentLoading />}>
            <FastQCCharts />
          </Suspense>
        </Tabs.TabPane>
        <Tabs.TabPane
          tab={i18n("FastQC.overrepresentedSequences")}
          key="overrepresented"
        >
          <Suspense fallback={<ContentLoading />}>
            <OverRepresentedSequences />
          </Suspense>
        </Tabs.TabPane>
        <Tabs.TabPane tab={i18n("FastQC.details")} key="details">
          <Suspense fallback={<ContentLoading />}>
            <FastQCDetails />
          </Suspense>
        </Tabs.TabPane>
      </Tabs>
    </>
  );
}

import React from "react";
import { Tabs } from "antd";

import { FastQCCharts } from "./FastQCCharts";
import { OverRepresentedSequences } from "./OverRepresentedSequences";
import { FastQCDetails } from "./FastQCDetails";

/**
 * React component to render tab menu for the FastQC results
 * @returns {JSX.Element}
 * @constructor
 */
export function FastQCMenu() {
  return (
    <Tabs defaultActiveKey="charts">
      <Tabs.TabPane
        tab={i18n("FastQC.charts")}
        key="charts"
        className="t-fastqc-modal-charts-tab"
      >
        <FastQCCharts />
      </Tabs.TabPane>
      <Tabs.TabPane
        tab={i18n("FastQC.overrepresentedSequences")}
        key="overrepresented"
        className="t-fastqc-modal-overrepresented-tab"
      >
        <OverRepresentedSequences />
      </Tabs.TabPane>
      <Tabs.TabPane
        tab={i18n("FastQC.details")}
        key="details"
        className="t-fastqc-modal-details-tab"
      >
        <FastQCDetails />
      </Tabs.TabPane>
    </Tabs>
  );
}

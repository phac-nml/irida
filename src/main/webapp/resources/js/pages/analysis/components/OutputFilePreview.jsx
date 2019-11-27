/*
 * This file renders the Info component for Bio Hansel
 */

import React, { Suspense, useContext, useEffect } from "react";
import { Tabs } from "antd";
import { AnalysisOutputsContext } from "../../../contexts/AnalysisOutputsContext";
import { getI18N } from "../../../utilities/i18n-utilities";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { AnalysisTabularPreview } from "./AnalysisTabularPreview";

const AnalysisTextPreview = React.lazy(() => import("./AnalysisTextPreview"));
const AnalysisJsonPreview = React.lazy(() => import("./AnalysisJsonPreview"));

const { TabPane } = Tabs;

export function OutputFilePreview() {
  const { analysisOutputsContext, getAnalysisOutputs } = useContext(
    AnalysisOutputsContext
  );

  useEffect(() => {
    getAnalysisOutputs();
  }, []);

  function jsonOutputPreview() {
    const jsonExtSet = new Set(["json"]);
    let jsonOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }

      if (jsonExtSet.has(output.fileExt)) {
        jsonOutput.unshift(
          <AnalysisJsonPreview output={output} key={output.filename} />
        );
      }
    }
    return jsonOutput;
  }

  function tabularOutputPreview() {
    const tabExtSet = new Set(["tab", "tsv", "tabular", "csv"]);

    let tabularOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }
      if (tabExtSet.has(output.fileExt)) {
        tabularOutput.unshift(
          <AnalysisTabularPreview output={output} key={output.filename} />
        );
      }
    }
    return tabularOutput;
  }

  function textOutputPreview() {
    const invalidExtSet = new Set(["tab", "tsv", "tabular", "csv", "json"]);

    let textOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }
      if (!invalidExtSet.has(output.fileExt)) {
        textOutput.unshift(
          <AnalysisTextPreview output={output} key={output.filename} />
        );
      }
    }
    return textOutput;
  }

  return analysisOutputsContext.outputs.length > 0 ? (
    <TabPaneContent title={getI18N("AnalysisOutputs.outputFilePreview")}>
      <Tabs defaultActiveKey="1" animated={false}>
        <TabPane
          tab={getI18N("AnalysisOutputs.tabularOutput")}
          key="tab-output"
        >
          {tabularOutputPreview()}
        </TabPane>
        <TabPane tab={getI18N("AnalysisOutputs.textOutput")} key="text-output">
          <Suspense fallback={<ContentLoading />}>
            {textOutputPreview()}
          </Suspense>
        </TabPane>
        <TabPane tab={getI18N("AnalysisOutputs.jsonOutput")} key="json-output">
          <Suspense fallback={<ContentLoading />}>
            {jsonOutputPreview()}
          </Suspense>
        </TabPane>
      </Tabs>
    </TabPaneContent>
  ) : (
    <ContentLoading />
  );
}

/*
 * This file renders the Info component for Bio Hansel
 */

import React, { useContext, useEffect } from "react";
import { Tabs } from "antd";
import { AnalysisOutputsContext } from "../../../../contexts/AnalysisOutputsContext";
import { getI18N } from "../../../../utilities/i18n-utilities";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";
import { renderJsonPreview } from "../../json-preview";
import { renderPlainTextPreview } from "../../plaintext-preview";
import $ from "jquery";
import { ContentLoading } from "../../../../components/loader/ContentLoading";
import { AnalysisTabularPreview } from "../AnalysisTabularPreview";

const { TabPane } = Tabs;

export function OutputFilePreview() {
  const { analysisOutputsContext, getAnalysisOutputs } = useContext(
    AnalysisOutputsContext
  );

  useEffect(() => {
    getAnalysisOutputs();
  }, []);

  function jsonOutputPreview() {
    const $tablesContainer = $("#js-file-preview-container");

    const jsonExtSet = new Set(["json"]);
    const baseAjaxUrl = `${window.TL.BASE_URL}ajax/analysis/`;
    let jsonOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }

      if (jsonExtSet.has(output.fileExt)) {
        jsonOutput.push(
          renderJsonPreview($tablesContainer, baseAjaxUrl, output)
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
        tabularOutput.push(
          <AnalysisTabularPreview output={output} key={output.filename} />
        );
      }
    }
    return tabularOutput;
  }

  function textOutputPreview() {
    const $tablesContainer = $("#js-file-preview-container");
    const tabExtSet = new Set(["tab", "tsv", "tabular", "csv"]);
    const jsonExtSet = new Set(["json"]);
    const baseAjaxUrl = `${window.TL.BASE_URL}ajax/analysis/`;
    let textOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }

      if (!jsonExtSet.has(output.fileExt) && !tabExtSet.has(output.fileExt)) {
        textOutput.push(
          renderPlainTextPreview($tablesContainer, baseAjaxUrl, output)
        );
      }
    }
    return textOutput;
  }

  return analysisOutputsContext.outputs.length > 0 ? (
    <TabPaneContent title={getI18N("AnalysisBioHansel.outputFilePreview")}>
      <Tabs defaultActiveKey="1" animated={false}>
        <TabPane tab="JSON Output" key="json-output">
          {jsonOutputPreview()}
        </TabPane>
        <TabPane tab="Tabular Output" key="tab-output">
          {tabularOutputPreview()}
        </TabPane>
        <TabPane tab="Text Output" key="text-output">
          {textOutputPreview()}
        </TabPane>
      </Tabs>
    </TabPaneContent>
  ) : (
    <ContentLoading />
  );
}

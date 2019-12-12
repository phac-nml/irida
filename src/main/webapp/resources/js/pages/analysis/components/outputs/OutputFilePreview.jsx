/*
 * This file renders the OutputFilePreview component for analyses
 */

import React, { Suspense, useContext, useEffect } from "react";
import { Button, Tabs } from "antd";
import { AnalysisContext } from "../../../../contexts/AnalysisContext";
import { AnalysisOutputsContext } from "../../../../contexts/AnalysisOutputsContext";
import { getI18N } from "../../../../utilities/i18n-utilities";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";
import { ContentLoading } from "../../../../components/loader/ContentLoading";
import { AnalysisTabularPreview } from "../AnalysisTabularPreview";
import { WarningAlert } from "../../../../components/alerts/WarningAlert";
import { SPACE_XS } from "../../../../styles/spacing";
import { downloadFilesAsZip } from "../../../../apis/analysis/analysis";
import { grey6 } from "../../../../styles/colors";

const AnalysisTextPreview = React.lazy(() => import("../AnalysisTextPreview"));
const AnalysisJsonPreview = React.lazy(() => import("../AnalysisJsonPreview"));

const { TabPane } = Tabs;

export default function OutputFilePreview() {
  const {
    analysisOutputsContext,
    getAnalysisOutputs,
    jsonExtSet,
    tabExtSet
  } = useContext(AnalysisOutputsContext);

  const { analysisContext } = useContext(AnalysisContext);

  useEffect(() => {
    if (analysisOutputsContext.outputs === null) {
      getAnalysisOutputs();
    }
  }, []);

  function jsonOutputPreview() {
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
    let textOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }
      if (!jsonExtSet.has(output.fileExt) && !tabExtSet.has(output.fileExt)) {
        textOutput.unshift(
          <AnalysisTextPreview output={output} key={output.filename} />
        );
      }
    }
    return textOutput;
  }

  return analysisOutputsContext.outputs !== null ? (
    <TabPaneContent title={getI18N("AnalysisOutputs.outputFilePreview")}>
      {analysisOutputsContext.outputs.length > 0 ? (
        <div>
          <Button
            style={{ marginBottom: SPACE_XS }}
            onClick={() =>
              downloadFilesAsZip(analysisContext.analysis.identifier)
            }
          >
            <span
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center"
              }}
            >
              <i
                class="fas fa-file-archive"
                style={{ color: grey6, marginRight: SPACE_XS }}
              ></i>
              {getI18N("AnalysisOutputs.downloadAllFiles")}
            </span>
          </Button>
          <Tabs defaultActiveKey="1" animated={false}>
            {analysisOutputsContext.fileTypes[0].hasTabularFile ? (
              <TabPane
                tab={getI18N("AnalysisOutputs.tabularOutput")}
                key="tab-output"
              >
                {tabularOutputPreview()}
              </TabPane>
            ) : null}

            {analysisOutputsContext.fileTypes[0].hasTextFile ? (
              <TabPane
                tab={getI18N("AnalysisOutputs.textOutput")}
                key="text-output"
              >
                <Suspense fallback={<ContentLoading />}>
                  {textOutputPreview()}
                </Suspense>
              </TabPane>
            ) : null}

            {analysisOutputsContext.fileTypes[0].hasJsonFile ? (
              <TabPane
                tab={getI18N("AnalysisOutputs.jsonOutput")}
                key="json-output"
              >
                <Suspense fallback={<ContentLoading />}>
                  {jsonOutputPreview()}
                </Suspense>
              </TabPane>
            ) : null}
          </Tabs>
        </div>
      ) : (
        <WarningAlert message={getI18N("AnalysisOutputs.noOutputsAvailable")} />
      )}
    </TabPaneContent>
  ) : (
    <ContentLoading />
  );
}

/**
 * @File component renders the analysis provenance for the
 * file the user has selected to view
 */

import React, { useContext, useEffect, useState } from "react";
import { Collapse, Descriptions, Layout } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import {
  blue10,
  blue2,
  blue3,
  blue4,
  blue5,
  blue6,
  blue7,
  blue8,
  blue9,
  grey1,
  grey3,
} from "../../../styles/colors";
import { getAnalysisProvenanceByFile } from "../../../apis/analysis/analysis";
import { TabPanelContent } from "../../../components/tabs/TabPanelContent";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisOutputsContext } from "../../../contexts/AnalysisOutputsContext";
import { WarningAlert } from "../../../components/alerts";
import { ContentLoading } from "../../../components/loader";

const { Panel } = Collapse;

export default function AnalysisProvenance() {
  const { analysisIdentifier } = useContext(AnalysisContext);
  const { analysisOutputsContext, getAnalysisOutputs } = useContext(
    AnalysisOutputsContext
  );
  const [provenance, setProvenance] = useState(null);
  const [previousExecutionToolsInfo, setPreviousExecutionToolsInfo] = useState(
    []
  );
  const [currFileName, setCurrFileName] = useState(null);

  // We use this to offset the margin for each tool
  // level and parameters
  const toolPanelMargin = 5;

  // We use this to display a color gradient
  // down the left hand side of the file provenance
  // to indicate which "level" the tool was run at
  const levelColors = [
    {
      0: blue10,
      1: blue9,
      2: blue8,
      3: blue7,
      4: blue6,
      5: blue5,
      6: blue4,
      7: blue3,
      8: blue2,
    },
  ];

  // Gets the analysis outputs on load if they have not
  // already been loaded
  useEffect(() => {
    if (analysisOutputsContext.outputs === null) {
      getAnalysisOutputs();
    }
  }, []);

  // Get the provenance for the file that the user selected
  function getProvenance(filename) {
    if (typeof filename !== "undefined") {
      if (
        (currFileName !== null && currFileName !== filename.toString()) ||
        provenance === null
      ) {
        setCurrFileName(filename.toString());
        getAnalysisProvenanceByFile(
          analysisIdentifier,
          filename.toString()
        ).then((data) => {
          setProvenance(data);
          setPreviousExecutionToolsInfo([]);
        });
      }
    }
  }

  // Returns a collapse panel for the tool and it's execution parameters.
  // Parameters are sorted alphabetically by label
  function displayToolWithExecutionParameters(tool, margin) {
    let parameters = [];
    let sortedParameters = tool.executionParameters.sort((a, b) =>
      a.parameterName > b.parameterName ? 1 : -1
    );

    for (let param of sortedParameters) {
      parameters.push(
        <Descriptions.Item
          label={param.parameterName}
          key={`${tool.toolName}-${param.parameterName}`}
          className="t-galaxy-parameter"
        >
          {param.parameterValue}
        </Descriptions.Item>
      );
    }

    return (
      <Collapse
        style={{
          border: "none",
          borderLeft: `8px ${getLevelColor(margin)} solid`,
          borderRadius: 0,
          backgroundColor: grey1,
        }}
        key={`collapse-${tool.toolName}-${Math.random()}`}
      >
        <Panel
          header={tool.toolName}
          key={tool.toolName}
          style={{
            backgroundColor: grey1,
            marginLeft: `${3 * margin}px`,
            borderBottom: 0,
            borderRadius: 0,
          }}
          className="t-tool-name"
        >
          {parameters.length > 0 ? (
            <Descriptions bordered column={2} size="small">
              {parameters}
            </Descriptions>
          ) : (
            ""
          )}
        </Panel>
      </Collapse>
    );
  }

  // Gets the color for the "level" of the execution
  // tool. Used to display a color gradient along the
  // left hand side of the provenance for the file to
  // indicate level
  function getLevelColor(margin) {
    const level = margin / toolPanelMargin;
    return levelColors[0][level] || grey3;
  }

  // Recursive function to get all the previous execution
  // tools and their respective execution parameters for
  // the tool that the output was created by
  function getPreviousExecutionTools(tool, margin) {
    let prevTools = tool.previousExecutionTools;

    // We either add the toolPanelMargin (5px) to the previous
    // margin or subtract depending on which "level" the tool
    // is to be displayed
    if (prevTools.length > 0) {
      margin = margin + toolPanelMargin;
    } else {
      margin = margin - toolPanelMargin;
    }

    for (let currTool of prevTools) {
      previousExecutionToolsInfo.push(
        displayToolWithExecutionParameters(currTool, margin)
      );
      getPreviousExecutionTools(currTool, margin);
    }
  }

  // Creates the panels for all the output files but
  // gets the provenance for only the output file that
  // the user is viewing the provenance for
  function getCreatedByToolPanels() {
    let panels = [];
    if (
      analysisOutputsContext.outputs !== null &&
      analysisOutputsContext.outputs.length > 0
    ) {
      for (let output of analysisOutputsContext.outputs) {
        {
          provenance !== null && currFileName === output.filename
            ? getPreviousExecutionTools(provenance.data.createdByTool, 0)
            : "";
        }
        panels.push(
          <Panel
            header={output.filename}
            key={output.filename}
            style={{ backgroundColor: grey1 }}
            className="t-file-header"
          >
            {currFileName === output.filename && provenance !== null
              ? [
                  displayToolWithExecutionParameters(
                    provenance.data.createdByTool,
                    0
                  ),
                  previousExecutionToolsInfo,
                ]
              : null}
          </Panel>
        );
      }
      return panels;
    }
  }

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPanelContent title={i18n("Analysis.provenance")}>
        {analysisOutputsContext.outputs !== null ? (
          analysisOutputsContext.outputs.length > 0 ? (
            <Collapse accordion onChange={(e) => getProvenance(e)}>
              {getCreatedByToolPanels()}
            </Collapse>
          ) : (
            <WarningAlert message={i18n("AnalysisProvenance.noFilesFound")} />
          )
        ) : (
          <ContentLoading />
        )}
      </TabPanelContent>
    </Layout>
  );
}

/**
 * @File component renders the analysis provenance for the
 * file the user has selected to view
 */

import React, { useEffect, useContext, useState } from "react";
import { Collapse, Descriptions, Layout } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { getAnalysisProvenanceByFile } from "../../../apis/analysis/analysis";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisOutputsContext } from "../../../contexts/AnalysisOutputsContext";

const { Panel } = Collapse;

export default function AnalysisProvenance() {
  const { analysisContext } = useContext(AnalysisContext);
  const { analysisOutputsContext, getAnalysisOutputs } = useContext(
    AnalysisOutputsContext
  );
  const [provenance, setProvenance] = useState(null);
  const [toolInfo, setToolInfo] = useState([]);
  const [currFileName, setCurrFileName] = useState(null);

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
        (provenance !== null &&
          !provenance.data.filename.includes(filename.toString())) ||
        provenance === null
      ) {
        setCurrFileName(filename.toString());
        getAnalysisProvenanceByFile(
          analysisContext.analysis.identifier,
          filename.toString()
        ).then(data => {
          setProvenance(data);
          setToolInfo([]);
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
        >
          {param.parameterValue}
        </Descriptions.Item>
      );
    }

    return (
      <Collapse
        style={{ border: "none", backgroundColor: grey1 }}
        key={`collapse-${tool.toolName}-${Math.random()}`}
      >
        <Panel
          header={tool.toolName}
          key={tool.toolname}
          style={{
            backgroundColor: grey1,
            marginLeft: `${3 * margin}px`,
            borderBottom: "0px"
          }}
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

  // Recursive function to get all the previous execution
  // tools and their respective execution parameters for
  // the tool that the output was created by
  function getPreviousExecutionTools(tool, margin) {
    let prevTools = tool.previousExecutionTools;

    // We either add 5px to the previous
    // margin or subtract depending on
    // which "level" the tool is at
    if (prevTools.length > 0) {
      margin = margin + 5;
    } else {
      margin = margin - 5;
    }

    for (let currTool of prevTools) {
      toolInfo.push(displayToolWithExecutionParameters(currTool, margin));
      getPreviousExecutionTools(currTool, margin);
    }
  }

  // Creates the panels for all the output files but
  // gets the provenance for only the output file that
  // the user is viewing the provenance for
  function getCreatedByToolPanels() {
    let panels = [];
    if (analysisOutputsContext.outputs !== null) {
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
          >
            {currFileName === output.filename && provenance !== null
              ? displayToolWithExecutionParameters(
                  provenance.data.createdByTool,
                  0
                )
              : null}
            {currFileName === output.filename ? toolInfo : null}
          </Panel>
        );
      }
      return panels;
    }
  }

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent title="Provenance">
        <Collapse accordion onChange={e => getProvenance(e)}>
          {getCreatedByToolPanels()}
        </Collapse>
      </TabPaneContent>
    </Layout>
  );
}

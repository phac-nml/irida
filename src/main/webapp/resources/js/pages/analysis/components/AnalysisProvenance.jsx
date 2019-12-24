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

  // Gets the analysis outputs on load if they have not
  // already been loaded
  useEffect(() => {
    if (analysisOutputsContext.outputs === null) {
      getAnalysisOutputs();
    }
  }, []);

  // Get the provenance for the file
  function getProvenance(e) {
    if (typeof e !== "undefined") {
      if (
        (provenance !== null &&
          !provenance.data.filename.includes(e.toString())) ||
        provenance === null
      ) {
        getAnalysisProvenanceByFile(
          analysisContext.analysis.identifier,
          e.toString()
        ).then(data => {
          setProvenance(data);
        });
      }
    }
  }

  // Displays the execution parameters for the tool
  function displayExecutionParameters(tool) {
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
      <Collapse style={{ border: "none" }}>
        <Panel
          header={tool.toolName}
          key={tool.toolname}
          style={{ backgroundColor: grey1 }}
        >
          <Descriptions bordered column={3} size="small">
            {parameters}
          </Descriptions>
        </Panel>
      </Collapse>
    );
  }

  // Creates the panel for the first tool and gets the execution parameters for it
  function getCreatedByToolPanels() {
    let c = [];
    if (analysisOutputsContext.outputs !== null) {
      for (let d of analysisOutputsContext.outputs) {
        c.push(
          <Panel
            header={d.filename}
            key={d.filename}
            style={{ backgroundColor: grey1 }}
          >
            {provenance !== null
              ? displayExecutionParameters(provenance.data.createdByTool)
              : ""}
          </Panel>
        );
      }
      return c;
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

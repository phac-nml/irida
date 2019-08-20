import React, { useContext } from "react";
import { Tabs } from "antd";

//import analysis components required by page
import { AnalysisDetails } from "./AnalysisDetails";
import { AnalysisSteps } from "./AnalysisSteps";
import { AnalysisProvenance } from "./AnalysisProvenance";
import { AnalysisOutputFiles } from "./AnalysisOutputFiles";
import { AnalysisPhylogeneticTree } from "./AnalysisPhylogeneticTree";
import { AnalysisBioHansel } from "./AnalysisBioHansel";
import { AnalysisSistr } from "./AnalysisSistr";
import { AnalysisError } from "./AnalysisError";

import { AnalysisContext, AnalysisProvider } from "../../../state/AnalysisState";
import { AnalysisDetailsProvider } from "../../../state/AnalysisDetailsContext";

const TabPane = Tabs.TabPane;

const analysisTypesWithAdditionalPage = [
  "bio_hansel Pipeline",
  "SISTR Pipeline",
  "SNVPhyl Phylogenomics Pipeline",
  "MentaLiST MLST Pipeline"
];

export default function Analysis() {
  const { context, analysisContextUpdateSubmissionName } = useContext(AnalysisContext);
  
  return (
    <>
      <div
        style={{ marginLeft: "15px", marginRight: "15px", marginTop: "15px" }}
      >
        <h1>{context.analysisName}</h1>
        <div>
          <AnalysisSteps />
        </div>
        <Tabs
          defaultActiveKey={
            context.isError ||
            (analysisTypesWithAdditionalPage.indexOf(context.workflowName) > -1 &&
              context.isCompleted)
              ? "0"
              : "3"
          }
          animated={false}
        >
          {context.isCompleted ? (
            [
              context.workflowName === "bio_hansel Pipeline" ? (
                <TabPane tab="bio_hansel" key="0">
                  <AnalysisBioHansel />
                </TabPane>
              ) : null,

              context.workflowName === "SISTR Pipeline" ? (
                <TabPane tab="sistr" key="0">
                  <AnalysisSistr />
                </TabPane>
              ) : null,

              context.workflowName === "SNVPhyl Phylogenomics Pipeline" ||
              context.workflowName == "MentaLiST MLST Pipeline" ? (
                <TabPane tab="Phylogenetic Tree" key="0">
                  <AnalysisPhylogeneticTree />
                </TabPane>
              ) : null,

              <TabPane tab="Output Files" key="1">
                <AnalysisOutputFiles />
              </TabPane>,

              <TabPane tab="Provenance" key="2">
                <AnalysisProvenance />
              </TabPane>
            ]
          ) : context.isError ? (
            <TabPane tab="Job Error" key="0">
              <AnalysisError />
            </TabPane>
          ) : null}
          <TabPane tab="Settings" key="3">
            <AnalysisProvider>
                <AnalysisDetailsProvider>
                    <AnalysisDetails />
                </AnalysisDetailsProvider>
            </AnalysisProvider>
          </TabPane>
        </Tabs>
      </div>
    </>
  );
}

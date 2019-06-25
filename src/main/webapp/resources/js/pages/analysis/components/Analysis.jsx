import React, { useContext } from "react";
import PropTypes from "prop-types";
import { Tabs } from "antd";

//import analysis components required by page
import { AnalysisDetails } from "./AnalysisDetails"
import { AnalysisSteps } from "./AnalysisSteps"
import { AnalysisProvenance } from "./AnalysisProvenance"
import { AnalysisOutputFiles } from "./AnalysisOutputFiles"
import { AnalysisPhylogeneticTree } from "./AnalysisPhylogeneticTree"
import { AnalysisBioHansel } from "./AnalysisBioHansel"
import { AnalysisSistr } from "./AnalysisSistr"
import { AnalysisError } from "./AnalysisError"

import { AnalysisContext } from '../../../state/AnalysisState'

const TabPane = Tabs.TabPane;

const analysisTypesWithAdditionalPage = [
    'bio_hansel Pipeline',
    'SISTR Pipeline',
    'SNVPhyl Phylogenomics Pipeline',
    'MentaLiST MLST Pipeline'
];

export default function Analysis() {
    const { state } = useContext(AnalysisContext);

    return (
        <>
            <div style={{marginLeft: "15px", marginRight: "15px", marginTop: "15px"}}>
                <h1>{state.analysisName}</h1>
                <div>
                    <AnalysisSteps />
                </div>
                <Tabs defaultActiveKey={
                    analysisTypesWithAdditionalPage.indexOf(state.workflowName) > -1 && state.isCompleted ?
                    "0" : "4"}
                    animated={false}
                >
                    {
                        state.isCompleted ?
                        [
                            state.workflowName === "bio_hansel Pipeline" ?
                                <TabPane tab="bio_hansel" key="0">
                                    <AnalysisBioHansel />
                                </TabPane>
                            :
                            null,

                            state.workflowName === "SISTR Pipeline" ?
                                <TabPane tab="sistr" key="0">
                                    <AnalysisSistr />
                                </TabPane>
                                :
                                null,

                            (state.workflowName === "SNVPhyl Phylogenomics Pipeline") || (state.workflowName == "MentaLiST MLST Pipeline") ?
                                <TabPane tab="Phylogenetic Tree" key="0">
                                    <AnalysisPhylogeneticTree />
                                </TabPane>
                                :
                                null,

                            <TabPane tab="Output Files" key="1">
                                <AnalysisOutputFiles />
                            </TabPane>,

                            <TabPane tab="Provenance" key="2">
                                <AnalysisProvenance />
                            </TabPane>
                        ]
                        :
                            state.isError ?
                                <TabPane tab="Job Error" key="3">
                                    <AnalysisError />
                                </TabPane>
                            : null
                    }
                    <TabPane tab="Settings" key="4">
                        <AnalysisDetails />
                    </TabPane>
                </Tabs>
            </div>
        </>
   );
}

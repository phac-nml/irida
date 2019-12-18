/*
 * This file renders the Serovar Predictions component for SISTR
 */

import React from "react";
import { BasicList } from "../../../../components/lists/BasicList";

import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function SerovarPredictions({ sistrResults }) {
  function serovarPredictions() {
    return [
      {
        title: i18n("AnalysisSistr.overallSerovar"),
        desc: sistrResults.serovar
      },
      {
        title: i18n("AnalysisSistr.antigenSerovar"),
        desc: sistrResults.serovar_antigen
      },
      {
        title: i18n("AnalysisSistr.cgmlstSerovar"),
        desc: sistrResults.serovar_cgmlst
      },
      {
        title: i18n("AnalysisSistr.serogroup"),
        desc: sistrResults.serogroup
      },
      {
        title: i18n("AnalysisSistr.h1"),
        desc: sistrResults.h1
      },
      {
        title: i18n("AnalysisSistr.h2"),
        desc: sistrResults.h2
      },
      {
        title: i18n("AnalysisSistr.oAntigen"),
        desc: sistrResults.o_antigen
      }
    ];
  }

  /*
   * Returns a simple list which displays labels and values
   * for the serovar predictions
   */
  return (
    <TabPaneContent title={i18n("AnalysisSistr.serovarPredictions")}>
      <BasicList dataSource={serovarPredictions()} />
    </TabPaneContent>
  );
}

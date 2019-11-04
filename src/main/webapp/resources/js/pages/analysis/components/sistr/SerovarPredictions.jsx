/*
 * This file renders the Serovar Predictions component for SISTR
 */

import React from "react";
import { BasicList } from "../../../../components/lists/BasicList";
import { getI18N } from "../../../../utilities/i18n-utilties";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function SerovarPredictions({ sistrResults }) {
  function serovarPredictions() {
    return [
      {
        title: getI18N("AnalysisSistr.overallSerovar"),
        desc: sistrResults.serovar
      },
      {
        title: getI18N("AnalysisSistr.antigenSerovar"),
        desc: sistrResults.serovar_antigen
      },
      {
        title: getI18N("AnalysisSistr.cgmlstSerovar"),
        desc: sistrResults.serovar_cgmlst
      },
      {
        title: getI18N("AnalysisSistr.serogroup"),
        desc: sistrResults.serogroup
      },
      {
        title: getI18N("AnalysisSistr.h1"),
        desc: sistrResults.h1
      },
      {
        title: getI18N("AnalysisSistr.h2"),
        desc: sistrResults.h2
      },
      {
        title: getI18N("AnalysisSistr.oAntigen"),
        desc: sistrResults.o_antigen
      }
    ];
  }

  /*
   * Returns a simple list which displays labels and values
   * for the serovar predictions
   */
  return (
    <TabPaneContent title={getI18N("AnalysisSistr.serovarPredictions")}>
      <BasicList dataSource={serovarPredictions()} />
    </TabPaneContent>
  );
}

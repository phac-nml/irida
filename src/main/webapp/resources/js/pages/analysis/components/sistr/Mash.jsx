/*
 * This file renders the Mash component for SISTR
 */

import React from "react";
import { BasicList } from "../../../../components/lists/BasicList";
import { getI18N } from "../../../../utilities/i18n-utilties";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function Mash({ sistrResults }) {
  function mash() {
    return [
      {
        title: getI18N("AnalysisSistr.subspecies"),
        desc: sistrResults.mash_subspecies
      },
      {
        title: getI18N("AnalysisSistr.serovar"),
        desc: sistrResults.mash_serovar
      },
      {
        title: getI18N("AnalysisSistr.matchingGenomeName"),
        desc: sistrResults.mash_genome
      },
      {
        title: getI18N("AnalysisSistr.mashDistance"),
        desc: sistrResults.mash_distance.toString()
      }
    ];
  }

  /*
   * Returns a simple list which displays labels and values
   * for the sistr mash data
   */
  return (
    <TabPaneContent title={getI18N("AnalysisSistr.mash")}>
      <BasicList dataSource={mash()} />
    </TabPaneContent>
  );
}

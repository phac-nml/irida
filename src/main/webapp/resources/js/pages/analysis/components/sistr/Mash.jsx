/*
 * This file renders the Mash component for SISTR
 */

import React from "react";
import { BasicList } from "../../../../components/lists/BasicList";

import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function Mash({ sistrResults }) {
  function mash() {
    return [
      {
        title: i18n("AnalysisSistr.subspecies"),
        desc: sistrResults.mash_subspecies
      },
      {
        title: i18n("AnalysisSistr.serovar"),
        desc: sistrResults.mash_serovar
      },
      {
        title: i18n("AnalysisSistr.matchingGenomeName"),
        desc: sistrResults.mash_genome
      },
      {
        title: i18n("AnalysisSistr.mashDistance"),
        desc: sistrResults.mash_distance.toString()
      }
    ];
  }

  /*
   * Returns a simple list which displays labels and values
   * for the sistr mash data
   */
  return (
    <TabPaneContent title={i18n("AnalysisSistr.mash")}>
      <BasicList dataSource={mash()} />
    </TabPaneContent>
  );
}

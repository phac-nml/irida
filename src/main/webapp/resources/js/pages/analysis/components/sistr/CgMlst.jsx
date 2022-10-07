/*
 * This file renders the cgMLST component for SISTR
 */

import React from "react";
import { BasicList } from "../../../../components/lists/BasicList";
import { TabPanelContent } from "../../../../components/tabs/TabPanelContent";

const max_cgMLST_count = 330; /* the maximum number of possible cgMLST alleles in SISTR */

export default function CgMlst({ sistrResults }) {
  function cgMLST330() {
    return [
      {
        title: i18n("AnalysisSistr.subspecies"),
        desc: sistrResults.cgmlst_subspecies,
      },
      {
        title: i18n("AnalysisSistr.matchingGenomeName"),
        desc: sistrResults.cgmlst_genome_match,
      },
      {
        title: i18n("AnalysisSistr.allelesMatchingGenome"),
        desc: `${sistrResults.cgmlst_matching_alleles}/${max_cgMLST_count}`,
      },
      {
        title: i18n("AnalysisSistr.percentMatching"),
        desc:
          sistrResults.cgmlst_distance !== null
            ? `${getPercentageFromDistance(sistrResults.cgmlst_distance) + ""}%`
            : "-",
      },
      {
        title: i18n("AnalysisSistr.allelesFoundGenome"),
        desc:
          sistrResults.cgmlst_found_loci !== null
            ? `${sistrResults.cgmlst_found_loci}/${max_cgMLST_count}` + ""
            : "-",
      },
      {
        title: i18n("AnalysisSistr.allelesFoundGenomePercent"),
        desc:
          sistrResults.cgmlst_found_loci !== null
            ? `${getPercentage(sistrResults.cgmlst_found_loci) + ""}%` + ""
            : "-",
      },
      {
        title: i18n("AnalysisSistr.cgmlstSequenceType"),
        desc:
          sistrResults.cgmlst_ST !== null ? sistrResults.cgmlst_ST + "" : "-",
      },
    ];
  }

  /*Converts decimal distance to percent similarity with 2 decimal points precision*/
  function getPercentageFromDistance(str) {
    return parseFloat((1 - str) * 100).toFixed(2);
  }

  /*Get percent value out of cgMLST allele counts out of 330 (maximum number of cgMLST alleles)*/
  function getPercentage(str) {
    return parseFloat((str / max_cgMLST_count) * 100).toFixed(2);
  }

  /*
   * Returns a simple list which displays labels and values
   * for the cgMLST data
   */
  return (
    <TabPanelContent title={i18n("AnalysisSistr.cgmlst330")}>
      <BasicList dataSource={cgMLST330()} />
    </TabPanelContent>
  );
}

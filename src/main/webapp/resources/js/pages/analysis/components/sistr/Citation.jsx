/*
 * This file renders the citation component for SISTR
 */

import React from "react";

import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function Citation() {
  /*
   * Returns the citation for the SISTR workflow
   */
  return (
    <TabPaneContent title={i18n("AnalysisSistr.citation")}>
      <a href="https://doi.org/10.1371/journal.pone.0147101" id="t-citation">
        {i18n("AnalysisSistr.citationLinkText")}
        <cite>{i18n("AnalysisSistr.plosOne")}</cite>
      </a>
    </TabPaneContent>
  );
}

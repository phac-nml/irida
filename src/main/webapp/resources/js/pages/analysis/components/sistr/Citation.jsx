/*
 * This file renders the citation component for SISTR
 */

import React from "react";

import { TabPanelContent } from "../../../../components/tabs/TabPanelContent";

export default function Citation() {
  /*
   * Returns the citation for the SISTR workflow
   */
  return (
    <TabPanelContent title={i18n("AnalysisSistr.citation")}>
      <a
        href="https://doi.org/10.1371/journal.pone.0147101"
        className="t-citation"
      >
        {i18n("AnalysisSistr.citationLinkText")}
        <cite>{i18n("AnalysisSistr.plosOne")}</cite>
      </a>
    </TabPanelContent>
  );
}

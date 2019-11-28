/*
 * This file renders the tree component
 */

import React from "react";
import { getI18N } from "../../../../utilities/i18n-utilities";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function Tree() {
  /*
   * Returns the tree
   */
  return (
    <TabPaneContent
      title={getI18N("AnalysisPhylogeneticTree.tree")}
    ></TabPaneContent>
  );
}

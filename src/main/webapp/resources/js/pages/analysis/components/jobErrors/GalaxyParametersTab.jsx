/*
 * This file renders the 'Galaxy Parameters' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays these parameters
 * in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { GalaxyParameters } from "./GalaxyParameters";

import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function GalaxyParametersTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors
}) {
  return (
    <TabPaneContent title={i18n("AnalysisError.galaxyParameters")}>
      {galaxyJobErrors.length > 1 ? (
        <PassTabs
          tabName="galaxy-parameters"
          currActiveKey={currActiveKey}
          updateActiveKey={updateActiveKey}
          galaxyJobErrors={galaxyJobErrors}
        />
      ) : (
        <GalaxyParameters galaxyJobErrors={galaxyJobErrors} />
      )}
    </TabPaneContent>
  );
}

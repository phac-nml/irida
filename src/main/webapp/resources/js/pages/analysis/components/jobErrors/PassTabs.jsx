/*
 * This file renders the `Pass` tabs if there are
 * multiple job errors.
 */

import React from "react";
import { Tabs } from "antd";
import { GalaxyJobInfo } from "./GalaxyJobInfo";
import { GalaxyParameters } from "./GalaxyParameters";
import { StandardErrorOutput } from "./StandardErrorOutput";

const { TabPane } = Tabs;

export function PassTabs({
  tabName,
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors,
  galaxyUrl,
}) {
  /* This function will display 'Pass' tabs within the tabpanes if there is
   * more than one error is returned for an analysis.
   */
  function showPassTabs(tabName) {
    // Since we only need the jobError indexes to generate the tabs we iterate '
    // over the keys instead of the whole galaxyJobErrors object
    return (
      <Tabs
        animated={false}
        onChange={(key) => updateActiveKey(key)}
        activeKey={`${tabName}-pass-${currActiveKey}`}
      >
        {Object.keys(galaxyJobErrors).map((key) => {
          const index = parseInt(key);
          if (tabName === "galaxy-parameters") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <GalaxyParameters
                  galaxyJobErrors={galaxyJobErrors}
                  currIndex={index}
                />
              </TabPane>
            );
          } else if (tabName === "job-error-info") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <GalaxyJobInfo
                  galaxyJobErrors={galaxyJobErrors}
                  galaxyUrl={galaxyUrl}
                  currIndex={index}
                />
              </TabPane>
            );
          } else if (tabName === "standard-error") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <StandardErrorOutput
                  galaxyError={galaxyJobErrors[index].standardError}
                />
              </TabPane>
            );
          } else if (tabName === "standard-out") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <StandardErrorOutput
                  galaxyError={galaxyJobErrors[index].standardOutput}
                />
              </TabPane>
            );
          }
        })}
      </Tabs>
    );
  }

  return <>{showPassTabs(tabName)}</>;
}

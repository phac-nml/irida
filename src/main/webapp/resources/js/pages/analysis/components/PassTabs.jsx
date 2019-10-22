import React from "react";
import { Tabs } from "antd";
import { GalaxyJobInfo } from "./GalaxyJobInfo";
import { GalaxyParameters } from "./GalaxyParameters";
import { StandardError } from "./StandardError";
import { StandardOutput } from "./StandardOutput";

const TabPane = Tabs.TabPane;

export function PassTabs(props) {
  /* This function will display 'Pass' tabs within the tabpanes if there is
   * more than one error is returned for an analysis.
   */
  function showPassTabs(tabName) {
    // Since we only need the jobError indexes to generate the tabs we iterate '
    // over the keys instead of the whole galaxyJobErrors object
    return (
      <Tabs
        animated={false}
        onChange={key => props.value.updateActiveKey(key)}
        activeKey={`${tabName}-pass-${props.value.currActiveKey}`}
      >
        {Object.keys(props.value.galaxyJobErrors).map(key => {
          const index = parseInt(key);
          if (tabName === "galaxy-parameters") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <GalaxyParameters
                  value={{
                    galaxyJobErrors: props.value.galaxyJobErrors,
                    currIndex: index
                  }}
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
                  value={{
                    galaxyJobErrors: props.value.galaxyJobErrors,
                    currIndex: index,
                    galaxyUrl: props.value.galaxyUrl
                  }}
                />
              </TabPane>
            );
          } else if (tabName === "standard-error") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <StandardError
                  value={{
                    galaxyJobErrors: props.value.galaxyJobErrors,
                    currIndex: index
                  }}
                />
              </TabPane>
            );
          } else if (tabName === "standard-out") {
            return (
              <TabPane
                tab={`Pass ${index + 1}`}
                key={`${tabName}-pass-${index + 1}`}
              >
                <StandardOutput
                  value={{
                    galaxyJobErrors: props.value.galaxyJobErrors,
                    currIndex: index
                  }}
                />
              </TabPane>
            );
          }
        })}
      </Tabs>
    );
  }

  return <>{showPassTabs(props.value.tabName)}</>;
}

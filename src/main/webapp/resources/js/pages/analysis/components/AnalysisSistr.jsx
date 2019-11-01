/*
 * This file renders the SISTR results which includes
 * the SISTR Information, Serovar Predicitons, cgMLST330,
 * and Mash
 */

import React, { Suspense, useContext, useEffect, useState } from "react";
import { Tabs, Typography } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getSistrResults } from "../../../apis/analysis/analysis";
import { SideTabs } from "../../../components/tabs/SideTabs";
import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { getI18N } from "../../../utilities/i18n-utilties";

const SistrInfo = React.lazy(() => import("./sistr/SistrInfo"));
const SerovarPredictions = React.lazy(() =>
  import("./sistr/SerovarPredictions")
);
const CgMlst = React.lazy(() => import("./sistr/CgMlst"));
const Mash = React.lazy(() => import("./sistr/Mash"));
const Citation = React.lazy(() => import("./sistr/Citation"));

const { Title } = Typography;
const TabPane = Tabs.TabPane;

export default function AnalysisSistr(props) {
  const { analysisContext } = useContext(AnalysisContext);
  const [sistrResults, setSistrResults] = useState(null);

  // On load gets the SISTR results
  useEffect(() => {
    getSistrResults(analysisContext.analysis.identifier).then(data => {
      setSistrResults(data);
    });
  }, []);

  /*
   * The following renders the components for the SISTR results side tabs
   */
  return sistrResults !== null ? (
    !sistrResults.parse_results_error ? (
      <SideTabs
        activeKey={
          props.defaultTabKey === "" || props.defaultTabKey === "sistr_typing"
            ? "sistr_info"
            : props.defaultTabKey
        }
        onChange={props.updateNav}
      >
        <TabPane
          tab={getI18N("AnalysisSistr.sistrInformation")}
          key="sistr_info"
        >
          <Suspense fallback={<ContentLoading />}>
            <SistrInfo
              sistrResults={sistrResults.result}
              sampleName={sistrResults.sampleName}
            />
          </Suspense>
        </TabPane>
        <TabPane
          tab={getI18N("AnalysisSistr.serovarPredictions")}
          key="serovar_predictions"
        >
          <Suspense fallback={<ContentLoading />}>
            <SerovarPredictions sistrResults={sistrResults.result} />
          </Suspense>
        </TabPane>
        <TabPane tab={getI18N("AnalysisSistr.cgmlst330")} key="cgmlst_330">
          <Suspense fallback={<ContentLoading />}>
            <CgMlst sistrResults={sistrResults.result} />
          </Suspense>
        </TabPane>
        <TabPane tab={getI18N("AnalysisSistr.mash")} key="mash">
          <Suspense fallback={<ContentLoading />}>
            <Mash sistrResults={sistrResults.result} />
          </Suspense>
        </TabPane>
        <TabPane tab="Citation" key="citation">
          <Suspense fallback={<ContentLoading />}>
            <Citation />
          </Suspense>
        </TabPane>
      </SideTabs>
    ) : (
      <WarningAlert message={getI18N("AnalysisSistr.resultsUnavailable")} />
    )
  ) : (
    <ContentLoading />
  );
}

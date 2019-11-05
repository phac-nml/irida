/*
 * This file renders the SISTR results which includes
 * the SISTR Information, Serovar Predicitons, cgMLST330,
 * and Mash
 */

import React, { Suspense, useContext, useEffect, useState } from "react";
import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getSistrResults } from "../../../apis/analysis/analysis";
import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { getI18N } from "../../../utilities/i18n-utilties";
import { SPACE_MD } from "../../../styles/spacing";

const SistrInfo = React.lazy(() => import("./sistr/SistrInfo"));
const SerovarPredictions = React.lazy(() =>
  import("./sistr/SerovarPredictions")
);
const CgMlst = React.lazy(() => import("./sistr/CgMlst"));
const Mash = React.lazy(() => import("./sistr/Mash"));
const Citation = React.lazy(() => import("./sistr/Citation"));
const { Content, Sider } = Layout;

export default function AnalysisSistr(props) {
  const { analysisContext } = useContext(AnalysisContext);
  const [sistrResults, setSistrResults] = useState(null);

  const pathRegx = new RegExp(/([a-zA-Z_0-9]+)$/);

  // On load gets the SISTR results
  useEffect(() => {
    getSistrResults(analysisContext.analysis.identifier).then(data => {
      setSistrResults(data);
    });
  }, []);

  /*
   * The following renders the components for the SISTR results tabs
   */
  return sistrResults !== null ? (
    !sistrResults.parse_results_error ? (
      <Layout>
        <Sider width={200} style={{ background: "#fff" }}>
          <Location>
            {props => {
              const keyname = props.location.pathname.match(pathRegx);
              return (
                <Menu
                  mode="vertical"
                  selectedKeys={[keyname ? keyname[1] : "sistr_info"]}
                >
                  <Menu.Item key="sistr_info">
                    <Link to="sistr_info">
                      {getI18N("AnalysisSistr.sistrInformation")}
                    </Link>
                  </Menu.Item>
                  <Menu.Item key="serovar_predictions">
                    <Link to="serovar_predictions">
                      {getI18N("AnalysisSistr.serovarPredictions")}
                    </Link>
                  </Menu.Item>
                  <Menu.Item key="cgmlst_330">
                    <Link to="cgmlst_330">
                      {getI18N("AnalysisSistr.cgmlst330")}
                    </Link>
                  </Menu.Item>
                  <Menu.Item key="mash">
                    <Link to="mash">{getI18N("AnalysisSistr.mash")}</Link>
                  </Menu.Item>
                  <Menu.Item key="citation">
                    <Link to="citation">
                      {getI18N("AnalysisSistr.citation")}
                    </Link>
                  </Menu.Item>
                </Menu>
              );
            }}
          </Location>
        </Sider>

        <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: "white" }}>
          <Content>
            <Suspense fallback={<ContentLoading />}>
              <Router>
                <SistrInfo
                  sistrResults={sistrResults.result}
                  sampleName={sistrResults.sampleName}
                  path="sistr_info"
                  default
                />
                <SerovarPredictions
                  sistrResults={sistrResults.result}
                  path="serovar_predictions"
                />
                <CgMlst sistrResults={sistrResults.result} path="cgmlst_330" />
                <Mash sistrResults={sistrResults.result} path="mash" />
                <Citation sistrResults={sistrResults.result} path="citation" />
              </Router>
            </Suspense>
          </Content>
        </Layout>
      </Layout>
    ) : (
      <WarningAlert message={getI18N("AnalysisSistr.resultsUnavailable")} />
    )
  ) : (
    <ContentLoading />
  );
}

/*
 * This file renders the SISTR results which includes
 * the SISTR Information(including Serovar Predicitons), cgMLST330,
 * and Mash
 */

import React, { Suspense, useContext, useEffect, useState } from "react";
import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getSistrResults } from "../../../apis/analysis/analysis";
import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { ContentLoading } from "../../../components/loader/ContentLoading";

import { setBaseUrl } from "../../../utilities/url-utilities";

import { SPACE_MD } from "../../../styles/spacing";
import { ANALYSIS, SISTR } from "../routes";
import { grey1 } from "../../../styles/colors";

const SistrInfo = React.lazy(() => import("./sistr/SistrInfo"));
const CgMlst = React.lazy(() => import("./sistr/CgMlst"));
const Mash = React.lazy(() => import("./sistr/Mash"));

const OutputFilePreview = React.lazy(() =>
  import("./outputs/OutputFilePreview")
);

const Citation = React.lazy(() => import("./sistr/Citation"));
const { Content, Sider } = Layout;

export default function AnalysisSistr() {
  const { analysisContext } = useContext(AnalysisContext);
  const [sistrResults, setSistrResults] = useState(null);

  const DEFAULT_URL =
    setBaseUrl(`/analysis/${analysisContext.analysis.identifier}/` + ANALYSIS.SISTR);
  const pathRegx = new RegExp(/([a-zA-Z_]+)$/);

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
        <Sider width={200} style={{ backgroundColor: grey1 }}>
          <Location>
            {props => {
              const keyname = props.location.pathname.match(pathRegx);
              return (
                <Menu
                  mode="vertical"
                  selectedKeys={[keyname ? keyname[1] : SISTR.INFO]}
                >
                  <Menu.Item key="info">
                    <Link to={`${DEFAULT_URL}/${SISTR.INFO}`}>
                      {i18n("AnalysisSistr.sistrInformation")}
                    </Link>
                  </Menu.Item>
                  <Menu.Item key="cgmlst">
                    <Link to={`${DEFAULT_URL}/${SISTR.CGMLST}`}>
                      {i18n("AnalysisSistr.cgmlst330")}
                    </Link>
                  </Menu.Item>
                  <Menu.Item key="mash">
                    <Link to={`${DEFAULT_URL}/${SISTR.MASH}`}>
                      {i18n("AnalysisSistr.mash")}
                    </Link>
                  </Menu.Item>
                  <Menu.Item key="citation">
                    <Link to={`${DEFAULT_URL}/${SISTR.CITATION}`}>
                      {i18n("AnalysisSistr.citation")}
                    </Link>
                  </Menu.Item>
                </Menu>
              );
            }}
          </Location>
        </Sider>

        <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
          <Content>
            <Suspense fallback={<ContentLoading />}>
              <Router>
                <SistrInfo
                  sistrResults={sistrResults.result}
                  sampleName={sistrResults.sampleName}
                  path={`${DEFAULT_URL}/${SISTR.INFO}`}
                  default
                />
                <CgMlst
                  sistrResults={sistrResults.result}
                  path={`${DEFAULT_URL}/${SISTR.CGMLST}`}
                />
                <Mash
                  sistrResults={sistrResults.result}
                  path={`${DEFAULT_URL}/${SISTR.MASH}`}
                />
                <Citation
                  sistrResults={sistrResults.result}
                  path={`${DEFAULT_URL}/${SISTR.CITATION}`}
                />
              </Router>
            </Suspense>
          </Content>
        </Layout>
      </Layout>
    ) : (
      <WarningAlert message={i18n("AnalysisSistr.resultsUnavailable")} />
    )
  ) : (
    <ContentLoading />
  );
}

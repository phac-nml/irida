/*
 * This file renders the SISTR results which includes
 * the SISTR Information(including Serovar Predicitons), cgMLST330,
 * and Mash
 */

import { Layout, Menu } from "antd";
import React, { Suspense, useContext, useEffect, useState } from "react";
import { Link, Route, Routes, useLocation } from "react-router-dom";
import { getSistrResults } from "../../../apis/analysis/analysis";
import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { grey1 } from "../../../styles/colors";

import { SPACE_MD } from "../../../styles/spacing";

import { setBaseUrl } from "../../../utilities/url-utilities";
import { ANALYSIS, SISTR } from "../routes";

const SistrInfo = React.lazy(() => import("./sistr/SistrInfo"));
const CgMlst = React.lazy(() => import("./sistr/CgMlst"));
const Mash = React.lazy(() => import("./sistr/Mash"));

const Citation = React.lazy(() => import("./sistr/Citation"));
const { Content, Sider } = Layout;

export default function AnalysisSistr({ baseUrl }) {
  const location = useLocation();
  const { analysisIdentifier } = useContext(AnalysisContext);
  const [sistrResults, setSistrResults] = useState(null);

  const DEFAULT_URL = setBaseUrl(
    `/analysis/${analysisIdentifier}/` + ANALYSIS.SISTR
  );
  const pathRegx = new RegExp(/([a-zA-Z_]+)$/);
  const keyname = location.pathname.match(pathRegx);

  // On load gets the SISTR results by causing the SistResult Object generation and conversion to Ajax Ojbect
  useEffect(() => {
    getSistrResults(analysisIdentifier).then((data) => {
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
          <Menu
            mode="vertical"
            selectedKeys={[keyname ? keyname[1] : SISTR.INFO]}
          >
            <Menu.Item key="info">
              <Link to={DEFAULT_URL}>
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
        </Sider>

        <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
          <Content>
            <Suspense fallback={<ContentLoading />}>
              <Routes>
                <Route
                  index
                  element={
                    <SistrInfo
                      sistrResults={sistrResults.result}
                      sampleName={sistrResults.sampleName}
                    />
                  }
                />
                <Route
                  path={SISTR.CGMLST}
                  element={<CgMlst sistrResults={sistrResults.result} />}
                />
                <Route
                  path={SISTR.MASH}
                  element={<Mash sistrResults={sistrResults.result} />}
                />
                <Route
                  path={SISTR.CITATION}
                  element={<Citation sistrResults={sistrResults.result} />}
                />
              </Routes>
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

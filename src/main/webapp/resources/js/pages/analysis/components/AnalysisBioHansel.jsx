/**
 * @File component renders the bio hansel results.
 */

import React, { Suspense, useContext, useEffect, useState } from "react";
import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";
import { SPACE_MD } from "../../../styles/spacing";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getDataViaChunks } from "../../../apis/analysis/analysis";
import { ContentLoading } from "../../../components/loader/ContentLoading";

import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { grey1 } from "../../../styles/colors";
import { ANALYSIS, BIOHANSEL } from "../routes";
import { AnalysisOutputsContext } from "../../../contexts/AnalysisOutputsContext";

const BioHanselInfo = React.lazy(() => import("./biohansel/BioHanselInfo"));
const OutputFilePreview = React.lazy(() =>
  import("./outputs/OutputFilePreview")
);

const { Content, Sider } = Layout;

export default function AnalysisBioHansel() {
  const { analysisContext } = useContext(AnalysisContext);
  const { analysisOutputsContext, getAnalysisOutputs } = useContext(
    AnalysisOutputsContext
  );
  const [bioHanselResults, setBioHanselResults] = useState(null);

  const BASE_URL = `${window.PAGE.base}/${ANALYSIS.BIOHANSEL}`;
  const pathRegx = new RegExp(/([a-zA-Z_]+)$/);

  // On load gets the bio hansel results. If the file is not found then set to undefined
  useEffect(() => {
    if (analysisOutputsContext.outputs === null) {
      getAnalysisOutputs();
    }
  }, []);

  useEffect(() => {
    getBioHanselResults();
  }, [analysisOutputsContext.outputs]);

  function getBioHanselResults() {
    if (analysisOutputsContext.outputs !== null) {
      const outputInfo = analysisOutputsContext.outputs.find(output => {
        return output.filename === "bio_hansel-results.json";
      });

      if (outputInfo !== undefined) {
        getDataViaChunks({
          submissionId: analysisContext.analysis.identifier,
          fileId: outputInfo.id,
          seek: 0,
          chunk: outputInfo.fileSizeBytes
        }).then(({ text }) => {
          const parsedResults = JSON.parse(text);
          setBioHanselResults(parsedResults[0]);
        });
      } else {
        setBioHanselResults(undefined);
      }
    }
  }

  return (
    <Layout>
      <Sider width={200} style={{ background: grey1 }}>
        <Location>
          {props => {
            const keyname = props.location.pathname.match(pathRegx);
            return (
              <Menu
                mode="vertical"
                selectedKeys={[keyname ? keyname[1] : BIOHANSEL.INFO]}
              >
                <Menu.Item key="info">
                  <Link to={`${BASE_URL}/${BIOHANSEL.INFO}`}>
                    {i18n("AnalysisBioHansel.bioHanselInformation")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="file_preview">
                  <Link to={`${BASE_URL}/${BIOHANSEL.FILE_PREVIEW}`}>
                    {i18n("AnalysisOutputs.outputFilePreview")}
                  </Link>
                </Menu.Item>
              </Menu>
            );
          }}
        </Location>
      </Sider>

      <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
        <Content>
          {typeof bioHanselResults === "undefined" ? (
            <WarningAlert
              message={i18n("AnalysisBioHansel.resultsUnavailable")}
            />
          ) : bioHanselResults !== null ? (
            <Suspense fallback={<ContentLoading />}>
              <Router>
                <BioHanselInfo
                  bioHanselResults={bioHanselResults}
                  path={`${BASE_URL}/${BIOHANSEL.INFO}`}
                  default
                />
                <OutputFilePreview
                  path={`${BASE_URL}/${BIOHANSEL.FILE_PREVIEW}`}
                />
              </Router>
            </Suspense>
          ) : (
            <ContentLoading />
          )}
        </Content>
      </Layout>
    </Layout>
  );
}

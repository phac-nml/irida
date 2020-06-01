/**
 * @File component renders a preview of output files
 */

import React, { useContext } from "react";
import { Layout } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { ANALYSIS, OUTPUT } from "../routes";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { AnalysisContext } from "../../../contexts/AnalysisContext";

const OutputFilePreview = React.lazy(() =>
  import("./outputs/OutputFilePreview")
);
const { Content } = Layout;

export default function AnalysisOutputFiles() {
  const { analysisContext } = useContext(AnalysisContext);
  const BASE_URL =
    setBaseUrl(`/analysis/${analysisContext.analysis.identifier}/` + ANALYSIS.OUTPUT);
  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <Content>
        <OutputFilePreview path={`${BASE_URL}/${OUTPUT.FILE_PREVIEW}`} />
      </Content>
    </Layout>
  );
}

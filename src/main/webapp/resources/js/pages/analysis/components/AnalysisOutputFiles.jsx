/**
 * @File component renders a preview of output files
 */

import React from "react";
import { Layout } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { ANALYSIS, OUTPUT } from "../routes";

const OutputFilePreview = React.lazy(() =>
  import("./outputs/OutputFilePreview")
);
const { Content } = Layout;

export default function AnalysisOutputFiles() {
  const BASE_URL = `${window.PAGE.base}/${ANALYSIS.OUTPUT}`;
  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <Content>
        <OutputFilePreview path={`${BASE_URL}/${OUTPUT.FILE_PREVIEW}`} />
      </Content>
    </Layout>
  );
}

/**
 * @File component renders a preview of output files
 */

import React from "react";
import { Layout } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { ANALYSIS, OUTPUT } from "../routes";
import { setBaseUrl } from "../../../utilities/url-utilities";

const OutputFilePreview = React.lazy(() =>
  import("./outputs/OutputFilePreview")
);
const { Content } = Layout;

export default function AnalysisOutputFiles() {
  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <Content>
        <OutputFilePreview path={setBaseUrl(`${ANALYSIS.OUTPUT}/${OUTPUT.FILE_PREVIEW}`)} />
      </Content>
    </Layout>
  );
}

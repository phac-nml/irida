/**
 * @File component renders an image preview of output files.
 */

import React, { useEffect, useState } from "react";
import { Divider } from "antd";

import { SPACE_XS } from "../../../styles/spacing";
import styled from "styled-components";
import { OutputFileHeader } from "../../../components/OutputFiles";
import { getImageFile } from "../../../apis/analysis/analysis";

const ImageOutputWrapper = styled.div`
  max-height: 300px;
  width: 100%;
  margin-bottom: ${SPACE_XS};
`;

export default function AnalysisImagePreview({ output }) {
  const [imageFile, setImageFile] = useState(null);
  const [imageExtension, setImageExtension] = useState(null);

  useEffect(() => {
    getImageFile(output.analysisSubmissionId, output.filename).then(({data}) => {
      setImageFile(data);
      let tokens = output.filename.split(".");
      setImageExtension(tokens[tokens.length-1]);
    });
  }, []);

  /*
   * Displays the image output as well
   * as the name of the file and a download button for
   * the file. Currently supports jpeg and png images.
   */
  function displayImageOutput() {
      return (
        <div>
          <OutputFileHeader output={output} />
          <ImageOutputWrapper
            id={`image-${output.filename.replace(".", "-")}`}
          >
            {imageExtension === "png" ?
              <img src={`data:image/png;base64,${imageFile}`} alt="" style={{height: "auto", maxWidth: "100%"}}/>
              :
              <img src={`data:image/jpeg;base64,${imageFile}`} alt="" style={{height: "auto", maxWidth: "100%"}}/>
            }
          </ImageOutputWrapper>
          <Divider />
        </div>
      );
  }
  return <>{displayImageOutput()}</>;
}

import * as React from "react";
import { BioSampleFileDetails } from "./utils";
import { BasicList } from "../../../../components/lists";

interface NcbiBioSampleFileProps {
  bioSample: BioSampleFileDetails;
}

function NcbiBioSampleFile({ bioSample }: NcbiBioSampleFileProps): JSX.Element {
  return (
    <>
      <BasicList
        dataSource={bioSample.details}
        grid={{ gutter: 16, column: 2 }}
      />
    </>
  );
}

export default NcbiBioSampleFile;

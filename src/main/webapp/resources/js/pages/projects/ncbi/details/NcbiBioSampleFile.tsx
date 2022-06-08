import * as React from "react";
import { BioSampleFileDetails } from "./utils";
import { BasicList } from "../../../../components/lists";
import BioSampleFile from "./BioSampleFile";

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
      {bioSample.files.pairs.map((file) => (
        <BioSampleFile key={file.label} bioSampleFile={file} />
      ))}
    </>
  );
}

export default NcbiBioSampleFile;

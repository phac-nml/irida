import * as React from "react";
import {PairedEndSequenceFile, SingleEndSequenceFile,} from "../../../../types/irida";

type File = SingleEndSequenceFile | PairedEndSequenceFile;

type BioSampleFileProps = {
  bioSampleFile: File;
};

function BioSampleFile({ bioSampleFile }: BioSampleFileProps) {
  if ("files" in bioSampleFile) {
    return <div>{bioSampleFile.label}</div>;
  } else {
    return <div>{bioSampleFile.label}</div>;
  }
}

export default BioSampleFile;

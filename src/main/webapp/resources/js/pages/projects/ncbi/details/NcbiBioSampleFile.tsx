import * as React from "react";
import { BioSampleFileDetails } from "./utils";

interface NcbiBioSampleFileProps {
  bioSample: BioSampleFileDetails;
}

function NcbiBioSampleFile({ bioSample }: NcbiBioSampleFileProps): JSX.Element {
  return <div>{bioSample.key}</div>;
}

export default NcbiBioSampleFile;

import * as React from "react";
import {
  PairedEndSequenceFile,
  SingleEndSequenceFile,
} from "../../../../types/irida";
import { Avatar, Space, Typography } from "antd";
import { blue6 } from "../../../../styles/colors";
import { SwapOutlined } from "@ant-design/icons";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";

function PairedEndLayout({
  file,
}: {
  file: PairedEndSequenceFile;
}): JSX.Element {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        width: `100%`,
      }}
    >
      <Space>
        <Avatar style={{ backgroundColor: blue6 }} icon={<SwapOutlined />} />
        {file.name}
      </Space>
      <Typography.Text type="secondary">
        {formatInternationalizedDateTime(file.createdDate)}
      </Typography.Text>
    </div>
  );
}

type BioSampleFileProps = {
  bioSampleFile: SingleEndSequenceFile | PairedEndSequenceFile;
};

function BioSampleFile({ bioSampleFile }: BioSampleFileProps) {
  if ("files" in bioSampleFile) {
    return <PairedEndLayout file={bioSampleFile} />;
  } else {
    return <div>{bioSampleFile.name}</div>;
  }
}

export default BioSampleFile;

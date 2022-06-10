import * as React from "react";
import { BioSampleFileDetails } from "./utils";
import { BasicList } from "../../../../components/lists";
import { List, Typography } from "antd";
import BioSampleFile from "./BioSampleFile";

interface NcbiBioSampleFileProps {
  bioSample: BioSampleFileDetails;
}

function NcbiBioSampleFiles({
  bioSample,
}: NcbiBioSampleFileProps): JSX.Element {
  return (
    <>
      <BasicList
        dataSource={bioSample.details}
        grid={{ gutter: 16, column: 2 }}
      />
      {/*{bioSample.files.pairs.map((file) => (*/}
      {/*  <BioSampleFile key={file.key} bioSampleFile={file} />*/}
      {/*))}*/}
      {bioSample.files.pairs.length > 0 && (
        <List
          header={<Typography.Text strong>Sequencing Files</Typography.Text>}
          dataSource={bioSample.files.pairs}
          renderItem={(item) => (
            <List.Item>
              <BioSampleFile bioSampleFile={item} />
            </List.Item>
          )}
        />
      )}
    </>
  );
}

export default NcbiBioSampleFiles;

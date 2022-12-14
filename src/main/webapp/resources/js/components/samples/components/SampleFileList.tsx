import React from "react";
import { notification, Space } from "antd";
import { useRemoveSampleFilesMutation } from "../../../apis/samples/samples";
import { useAppSelector } from "../../../hooks/useState";

import { GenomeAssemblyList } from "./GenomeAssemblyList";
import { SequencingObjectList } from "./SequencingObjectList";
import { SPACE_XS } from "../../../styles/spacing";
import { HEADER_HEIGHT_WITH_PADDING } from "./ViewerHeader";

/**
 * React component to display, remove, download files
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFileList() {
  const [removeSampleFilesFromSample] = useRemoveSampleFilesMutation();

  const { sample } = useAppSelector((state) => state.sampleReducer);
  const { files } = useAppSelector((state) => state.sampleFilesReducer);

  /*
  Remove sequencing objects and/or genome assembly objects from sample
   */
  const removeSampleFiles = ({
    fileObjectId,
    type,
  }: {
    fileObjectId: number;
    type: string;
  }) => {
    removeSampleFilesFromSample({
      sampleId: sample.identifier,
      fileObjectId,
      type,
    })
      .unwrap()
      .then(({ message }: { message: string }) => {
        notification.success({ message });
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  return (
    <Space
      size="large"
      direction="vertical"
      style={{
        height: `calc(80vh - ${HEADER_HEIGHT_WITH_PADDING}px - 157px)`,
        overflowY: "auto",
        width: `100%`,
        marginTop: SPACE_XS,
      }}
      className="t-filelist-scroll"
    >
      {(files.singles || files.paired || files.fast5) && (
        <SequencingObjectList removeSampleFiles={removeSampleFiles} />
      )}
      {files.assemblies && (
        <GenomeAssemblyList removeSampleFiles={removeSampleFiles} />
      )}
    </Space>
  );
}

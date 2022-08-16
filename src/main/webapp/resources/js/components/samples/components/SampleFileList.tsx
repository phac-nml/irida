import React from "react";
import { notification, Space } from "antd";
import { useRemoveSampleFilesMutation } from "../../../apis/samples/samples";
import { removeFileObjectFromSample } from "../sampleFilesSlice";
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";
import {
  setDefaultGenomeAssembly,
  setDefaultSequencingObject,
} from "../sampleSlice";
import { GenomeAssemblyList } from "./GenomeAssemblyList";
import { SequencingObjectList } from "./SequencingObjectList";

/**
 * React component to display, remove, download files
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFileList() {
  const dispatch = useAppDispatch();
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
        dispatch(removeFileObjectFromSample({ fileObjectId, type }));

        if (
          type === "sequencingObject" &&
          sample.defaultSequencingObject?.identifier === fileObjectId
        ) {
          dispatch(setDefaultSequencingObject(null));
        }

        if (
          type === "assembly" &&
          sample.defaultGenomeAssembly?.identifier === fileObjectId
        ) {
          dispatch(setDefaultGenomeAssembly(null));
        }
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  return (
    <Space
      size="large"
      direction="vertical"
      style={{ maxHeight: "500px", overflowY: "auto", width: `100%` }}
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

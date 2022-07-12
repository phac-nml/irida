import React from "react";
import { notification, Space } from "antd";
import { useRemoveSampleFilesMutation } from "../../../apis/samples/samples";
import { removeFileObjectFromSample } from "../sampleFilesSlice";
import { RootStateOrAny, useDispatch, useSelector } from "react-redux";
import { setDefaultSequencingObject } from "../sampleSlice";
import { GenomeAssemblyList } from "./GenomeAssemblyList";
import { SequencingObjectList } from "./SequencingObjectList";

/**
 * React component to display, remove, download files
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFileList() {
  const dispatch = useDispatch();
  const [removeSampleFilesFromSample] = useRemoveSampleFilesMutation();

  const { sample } = useSelector(
    (state: RootStateOrAny) => state.sampleReducer
  );
  const { files } = useSelector(
    (state: RootStateOrAny) => state.sampleFilesReducer
  );

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
      .then(({ data }) => {
        notification.success({ message: data.message });
        dispatch(removeFileObjectFromSample({ fileObjectId, type }));

        if (sample.defaultsequencingObject?.identifier === fileObjectId) {
          dispatch(setDefaultSequencingObject(null));
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

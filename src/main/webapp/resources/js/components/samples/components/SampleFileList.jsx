import React from "react";
import { notification, Space } from "antd";
import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";
import { SingleEndFileRenderer } from "../../sequence-files/SingleEndFileRenderer";
import { PairedFileRenderer } from "../../sequence-files/PairedFileRenderer";
import {
  downloadGenomeAssemblyFile,
  downloadSequencingObjectFile,
  useRemoveSampleFilesMutation,
} from "../../../apis/samples/samples";

import { removeFileObjectFromSample } from "../sampleFilesSlice";
import { useDispatch } from "react-redux";

/**
 * React component to display, remove, download files
 *
 * @param files The list of files to display
 * @param sampleId The sample identifier
 * @param modifiable If the sample can be modified by the user or not
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFileList({ files, sampleId, modifiable }) {
  const dispatch = useDispatch();
  const [removeSampleFilesFromSample] = useRemoveSampleFilesMutation();

  /*
  Download sequence files (paired, single, fast5)
 */
  const downloadSequenceFile = ({ sequencingObjectId, sequenceFileId }) => {
    notification.success({
      message: i18n("SampleFiles.startingSequenceFileDownload"),
    });
    downloadSequencingObjectFile({ sequencingObjectId, sequenceFileId });
  };

  /*
   Download genome assembly files
   */
  const downloadAssemblyFile = ({ sampleId, genomeAssemblyId }) => {
    notification.success({
      message: i18n("SampleFiles.startingAssemblyDownload"),
    });
    downloadGenomeAssemblyFile({ sampleId, genomeAssemblyId });
  };

  /*
  Remove sequencingobjects and/or genomeassembly objects from sample
   */
  const removeSampleFiles = ({ fileObjectId, type }) => {
    removeSampleFilesFromSample({
      sampleId: sampleId,
      fileObjectId,
      type,
    })
      .then(({ data }) => {
        notification.success({ message: data.message });
        dispatch(removeFileObjectFromSample({ fileObjectId, type }));
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  return (
    <div style={{ maxHeight: "400px", overflowY: "auto" }}>
      <Space size="large" direction="vertical" style={{ width: `100%` }}>
        {files.singles && (
          <SequenceFileTypeRenderer title={i18n("SampleFiles.singles")}>
            <SingleEndFileRenderer
              files={files.singles}
              sampleId={sampleId}
              downloadSequenceFile={downloadSequenceFile}
              removeSampleFiles={removeSampleFiles}
            />
          </SequenceFileTypeRenderer>
        )}
        {files.paired && (
          <SequenceFileTypeRenderer title={i18n("SampleFiles.paired")}>
            {files.paired.map((pair) => (
              <PairedFileRenderer
                key={`pair-${pair.identifier}`}
                pair={pair}
                sampleId={sampleId}
                downloadSequenceFile={downloadSequenceFile}
                removeSampleFiles={removeSampleFiles}
              />
            ))}
          </SequenceFileTypeRenderer>
        )}
        {files.fast5 && (
          <SequenceFileTypeRenderer title={i18n("SampleFiles.fast5")}>
            <SingleEndFileRenderer
              files={files.fast5}
              sampleId={sampleId}
              downloadSequenceFile={downloadSequenceFile}
              removeSampleFiles={removeSampleFiles}
            />
          </SequenceFileTypeRenderer>
        )}
        {files.assemblies && (
          <SequenceFileTypeRenderer title={i18n("SampleFiles.assemblies")}>
            <SingleEndFileRenderer
              files={files.assemblies}
              fastqcResults={false}
              sampleId={sampleId}
              downloadAssemblyFile={downloadAssemblyFile}
              removeSampleFiles={removeSampleFiles}
            />
          </SequenceFileTypeRenderer>
        )}
      </Space>
    </div>
  );
}

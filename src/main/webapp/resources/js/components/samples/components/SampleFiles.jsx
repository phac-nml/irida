import React from "react";
import { Empty, notification, Space } from "antd";
import { IconLoading } from "../../icons/Icons";
import { fetchSampleFiles } from "../../../apis/samples/samples";
import { PairedFileRenderer } from "../../sequence-files/PairedFileRenderer";
import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";
import { SingleEndFileRenderer } from "../../sequence-files/SingleEndFileRenderer";
import { DragUpload } from "../../files/DragUpload";
import { useDispatch, useSelector } from "react-redux";
import { setSampleFiles } from "../sampleFilesSlice";

/**
 * React component to display sample files.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFiles() {
  const { sample, projectId } = useSelector((state) => state.sampleReducer);
  const { files, loading } = useSelector((state) => state.sampleFilesReducer);
  const dispatch = useDispatch();

  React.useEffect(() => {
    fetchSampleFiles({ sampleId: sample.identifier, projectId })
      .then((data) => {
        dispatch(setSampleFiles(data));
      })
      .catch((e) => notification.error({ message: e }));
  }, [sample.identifier, projectId]);

  const options = {
    multiple: true,
    showUploadList: true,
    action: "",
    accept: ".fasta, .fastq, .fast5, .fastq.gz",
    beforeUpload(file) {
      if (file.name.includes(".fasta")) {
        console.log("Valid file");
      } else {
        console.log("invalid");
        return false;
      }
    },
  };

  return loading ? (
    <IconLoading />
  ) : Object.keys(files).length !== 0 ? (
    <Space size={`large`} direction={`vertical`} style={{ width: `100%` }}>
      <DragUpload
        className="t-upload-sample-files"
        uploadText={i18n("SampleFiles.uploadText")}
        uploadHint={i18n("SampleFiles.uploadHint")}
        options={options}
      />
      {files.singles && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.singles")}>
          <SingleEndFileRenderer
            files={files.singles}
            sampleId={sample.identifier}
          />
        </SequenceFileTypeRenderer>
      )}
      {files.paired && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.paired")}>
          {files.paired.map((pair) => (
            <PairedFileRenderer
              key={`pair-${pair.identifier}`}
              pair={pair}
              sampleId={sample.identifier}
            />
          ))}
        </SequenceFileTypeRenderer>
      )}
      {files.fast5 && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.fast5")}>
          <SingleEndFileRenderer
            files={files.fast5}
            sampleId={sample.identifier}
          />
        </SequenceFileTypeRenderer>
      )}
      {files.assemblies && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.assemblies")}>
          <SingleEndFileRenderer
            files={files.assemblies}
            fastqcResults={false}
          />
        </SequenceFileTypeRenderer>
      )}
    </Space>
  ) : (
    <Space size={`large`} direction={`vertical`} style={{ width: `100%` }}>
      <DragUpload
        className="t-upload-sample-files"
        uploadText={i18n("SampleFiles.uploadText")}
        uploadHint={i18n("SampleFiles.uploadHint")}
        options={options}
      />
      <Empty description={i18n("SampleFiles.no-files")} />
    </Space>
  );
}

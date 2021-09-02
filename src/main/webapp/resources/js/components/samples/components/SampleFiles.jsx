import React from "react";
import { Empty, notification, Space } from "antd";
import { IconLoading } from "../../icons/Icons";
import { fetchSampleFiles } from "../../../apis/samples/samples";
import { PairedFileRenderer } from "../../sequence-files/PairedFileRenderer";
import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";
import { SingleEndFileRenderer } from "../../sequence-files/SingleEndFileRenderer";
import { DragUpload } from "../../files/DragUpload";

/**
 * React component to display sample files.
 *
 * @param id - sample identifier
 * @param projectId - project identifier
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFiles({ id, projectId }) {
  const [loading, setLoading] = React.useState(true);
  const [files, setFiles] = React.useState();

  React.useEffect(() => {
    fetchSampleFiles({ sampleId: id, projectId })
      .then((data) => {
        /*
      Remove any file types that do not have associated files.
       */
        Object.keys(data).forEach(
          (key) => !data[key].length && delete data[key]
        );
        setFiles(data);
        setLoading(false);
      })
      .catch((e) => notification.error({ message: e }));
  }, [id, projectId]);

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
          <SingleEndFileRenderer files={files.singles} sampleId={id} />
        </SequenceFileTypeRenderer>
      )}
      {files.paired && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.paired")}>
          {files.paired.map((pair) => (
            <PairedFileRenderer
              key={`pair-${pair.identifier}`}
              pair={pair}
              sampleId={id}
            />
          ))}
        </SequenceFileTypeRenderer>
      )}
      {files.fast5 && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.fast5")}>
          <SingleEndFileRenderer files={files.fast5} sampleId={id} />
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
    <Empty description={i18n("SampleFiles.no-files")} />
  );
}

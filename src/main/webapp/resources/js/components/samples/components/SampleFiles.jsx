import React from "react";
import { Empty, notification, Space } from "antd";
import { IconLoading } from "../../icons/Icons";
import { fetchSampleFiles } from "../../../apis/samples/samples";
import { PairedFileRenderer } from "../../sequence-files/PairedFileRenderer";
import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";
import { SingleEndFileRenderer } from "../../sequence-files/SingleEndFileRenderer";

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

  return loading ? (
    <IconLoading />
  ) : Object.keys(files).length !== 0 ? (
    <Space size={`large`} direction={`vertical`} style={{ width: `100%` }}>
      {files.singles && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.singles")}>
          <SingleEndFileRenderer files={files.singles} />
        </SequenceFileTypeRenderer>
      )}
      {files.paired && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.paired")}>
          {files.paired.map((pair) => (
            <PairedFileRenderer key={`pair-${pair.identifier}`} pair={pair} />
          ))}
        </SequenceFileTypeRenderer>
      )}
      {files.fast5 && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.fast5")}>
          <SingleEndFileRenderer files={files.fast5} />
        </SequenceFileTypeRenderer>
      )}
      {files.assemblies && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.assemblies")}>
          <SingleEndFileRenderer files={files.assemblies} />
        </SequenceFileTypeRenderer>
      )}
    </Space>
  ) : (
    <Empty description={i18n("SampleFiles.no-files")} />
  );
}

import { Empty, List } from "antd";
import { SequencingRunFileCard } from "./SequencingRunFileCard";
import React from "react";
import { useDrop } from "react-dnd";
import { addFile, updateSample } from "../services/runReducer";
import { useDispatch } from "react-redux";

/**
 * React component to render the list of sequencing run files.
 * @param {array} samples - list of samples
 * @param {array} files - list of sequencing run files
 * @returns {JSX.Element} - Returns a list component
 */
export function SequencingRunFilesList({ samples, files }) {
  const dispatch = useDispatch();

  const [{ canDrop, isOver }, drop] = useDrop({
    accept: "card",
    canDrop: (item, monitor) => {
      if (item.prevIndex === null) {
        return false;
      } else {
        return true;
      }
    },
    drop: (item) => {
      const { file, prevIndex } = item;
      const prevSample = samples[prevIndex];
      const newSample = {
        sampleName: prevSample.sampleName,
        forwardSequenceFile:
          prevSample.forwardSequenceFile?.id === file.id
            ? prevSample.reverseSequenceFile
            : prevSample.forwardSequenceFile,
        reverseSequenceFile: null,
      };
      dispatch(updateSample(newSample, prevIndex));
      dispatch(addFile(file.id));
    },
  });

  return (
    <div ref={drop}>
      {files.length === 0 ? (
        <Empty
          description={i18n("SequencingRunCreateSamplesPage.empty")}
          image={Empty.PRESENTED_IMAGE_SIMPLE}
        />
      ) : (
        <List
          grid={{ column: 1 }}
          dataSource={files}
          renderItem={(file) => {
            return (
              <List.Item>
                <SequencingRunFileCard file={file}>
                  {file.fileName}
                </SequencingRunFileCard>
              </List.Item>
            );
          }}
        />
      )}
    </div>
  );
}

import { Empty, List } from "antd";
import { SequencingRunFileCard } from "./SequencingRunFileCard";
import React from "react";
import { useDrop } from "react-dnd";
import { addFile, removeFileFromSample } from "../services/runReducer";
import { useDispatch } from "react-redux";

/**
 * React component to render the list of samples and sequencing run files.
 * @param {array} samples - list of samples
 * @param {array} files - list of sequencing run files
 * @returns {JSX.Element} - Returns a list component
 */
export function SequencingRunFilesList({ samples, files }) {
  const dispatch = useDispatch();

  const [{ isOver }, drop] = useDrop({
    accept: "card",
    canDrop: (item) => {
      if (item.prevSampleIndex === null) {
        return false;
      } else {
        return true;
      }
    },
    drop: (item) => {
      const { file, prevSampleIndex, prevPairIndex } = item;

      //remove file from previous sample
      dispatch(removeFileFromSample(file.id, prevSampleIndex, prevPairIndex));

      //add file to sequencing run file list
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

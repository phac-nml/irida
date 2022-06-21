import { Empty, List } from "antd";
import React from "react";
import { SequencingRunSample } from "./SequencingRunSample";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { addSample } from "../services/runReducer";
import { useDispatch } from "react-redux";

/**
 * React component to render the samples list.
 * @param {array} samples - list of samples
 * @returns {JSX.Element} - Returns a sample list component
 */
export function SequencingRunSamplesList({ samples }) {
  const dispatch = useDispatch();

  const addNewSample = () => {
    dispatch(
      addSample({
        sampleName: "New Sample",
        forwardSequenceFile: null,
        reverseSequenceFile: null,
      })
    );
  };

  return (
    <>
      <Empty
        style={{ paddingBottom: "15px" }}
        description=""
        imageStyle={{ display: "none" }}
      >
        <AddNewButton
          onClick={addNewSample}
          text={i18n("SequencingRunSamplesList.empty.button")}
        />
      </Empty>
      {samples.length > 0 && (
        <List
          grid={{ column: 1 }}
          dataSource={samples}
          renderItem={(sample, index) => {
            return (
              <List.Item>
                <SequencingRunSample
                  samples={samples}
                  sample={sample}
                  index={index}
                />
              </List.Item>
            );
          }}
        />
      )}
    </>
  );
}

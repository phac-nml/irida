import React from "react";
import { useDrop } from "react-dnd";
import { Button, Card, Col, Row } from "antd";
import {
  IconArrowLeft,
  IconArrowRight,
  IconRemove,
  IconSwap,
} from "../../../components/icons/Icons";
import { grey1 } from "../../../styles/colors";
import { FONT_COLOR_PRIMARY } from "../../../styles/fonts";
import { SequencingRunFileCard } from "./SequencingRunFileCard";
import { SPACE_LG } from "../../../styles/spacing";
import {
  addFile,
  deleteSample,
  removeFile,
  updateSample,
} from "../services/runReducer";
import { useDispatch } from "react-redux";

/**
 * React component to render a sample.
 * @param {array} samples - list of samples
 * @param {object} sample - the sample
 * @param {number} index - the index of the sample in the samples array
 * @returns {JSX.Element} - Returns a sample component
 */
export function SequencingRunSample({ samples, sample, index }) {
  const dispatch = useDispatch();

  const removeSample = () => {
    if (sample.forwardSequenceFile !== null)
      dispatch(addFile(sample.forwardSequenceFile.id));
    if (sample.reverseSequenceFile !== null)
      dispatch(addFile(sample.reverseSequenceFile.id));
    dispatch(deleteSample(index));
  };

  const switchPair = () => {
    const newSample = {
      sampleName: sample.sampleName,
      forwardSequenceFile: sample.reverseSequenceFile,
      reverseSequenceFile: sample.forwardSequenceFile,
    };
    dispatch(updateSample(newSample, index));
  };

  const [{ canDrop, isOver }, drop] = useDrop({
    accept: "card",
    drop: (item) => {
      const { file, prevIndex } = item;

      //remove file from previous location
      if (prevIndex === null) {
        dispatch(removeFile(file.id));
      } else {
        const prevSample = samples[prevIndex];
        const newSample = {
          sampleName: prevSample.sampleName,
          forwardSequenceFile:
            file.id === prevSample.forwardSequenceFile?.id
              ? prevSample.reverseSequenceFile
              : prevSample.forwardSequenceFile,
          reverseSequenceFile: null,
        };
        dispatch(updateSample(newSample, prevIndex));
      }

      //add file to target location
      if (sample.forwardSequenceFile === null) {
        const newSample = {
          sampleName: sample.sampleName,
          forwardSequenceFile: file,
          reverseSequenceFile: sample.reverseSequenceFile,
        };
        dispatch(updateSample(newSample, index));
      } else if (sample.reverseSequenceFile === null) {
        const newSample = {
          sampleName: sample.sampleName,
          forwardSequenceFile: sample.forwardSequenceFile,
          reverseSequenceFile: file,
        };
        dispatch(updateSample(newSample, index));
      } else {
        //do nothing
      }
    },
  });

  return (
    <Card
      title={sample.sampleName}
      ref={drop}
      extra={
        <Button
          onClick={removeSample}
          style={{ border: "none" }}
          icon={<IconRemove />}
        />
      }
    >
      <Row align="middle" justify="center">
        {sample.forwardSequenceFile !== null && (
          <>
            <Col flex="75px">
              <IconArrowRight
                style={{
                  fontSize: "2em",
                  flex: 1,
                  padding: SPACE_LG,
                  color: grey1,
                  backgroundColor: FONT_COLOR_PRIMARY,
                }}
              />
            </Col>
            <Col flex="auto">
              <SequencingRunFileCard
                file={sample.forwardSequenceFile}
                index={index}
              >
                {sample.forwardSequenceFile.fileName}
              </SequencingRunFileCard>
            </Col>
          </>
        )}
        {sample.reverseSequenceFile !== null && (
          <>
            <Col span={2} offset={1}>
              <Button
                onClick={switchPair}
                style={{ border: "none" }}
                icon={<IconSwap />}
              />
            </Col>
            <Col flex="75px">
              <IconArrowLeft
                style={{
                  fontSize: "2em",
                  flex: 1,
                  padding: SPACE_LG,
                  color: grey1,
                  backgroundColor: FONT_COLOR_PRIMARY,
                }}
              />
            </Col>
            <Col flex="auto">
              <SequencingRunFileCard
                file={sample.reverseSequenceFile}
                index={index}
              >
                {sample.reverseSequenceFile.fileName}
              </SequencingRunFileCard>
            </Col>
          </>
        )}
      </Row>
    </Card>
  );
}

import React from "react";
import { useDrop } from "react-dnd";
import { Button, Card, Col, Empty, Row, Skeleton, Typography } from "antd";
import { IconRemove } from "../../../components/icons/Icons";
import {
  addFile,
  addFileToNewPairInSample,
  removeFile,
  removeFileFromSample,
  removeSample,
} from "../services/runReducer";
import { useDispatch } from "react-redux";
import { SequencingRunSamplePair } from "./SequencingRunSamplePair";

/**
 * React component to render a sample.
 * @param {array} samples - list of samples
 * @param {object} sample - the sample
 * @param {number} sampleIndex - the index of the sample in the sample array
 * @returns {JSX.Element} - Returns a sample component
 */
export function SequencingRunSample({ samples, sample, sampleIndex }) {
  const dispatch = useDispatch();

  const removeSampleFromList = () => {
    sample.pairs.map((pair) => {
      if (pair.forward !== null) {
        dispatch(addFile(pair.forward.id));
      }
      if (pair.reverse !== null) {
        dispatch(addFile(pair.reverse.id));
      }
    });
    dispatch(removeSample(sampleIndex));
  };

  const [{ isOver: isDropOnNewPairOver }, dropOnNewPair] = useDrop({
    accept: "card",
    collect: (monitor) => ({
      isOver: monitor.isOver(),
    }),
    drop: (item) => {
      const { file, prevSampleIndex, prevPairIndex } = item;

      if (prevSampleIndex === null) {
        //remove file from sequencing files list
        dispatch(removeFile(file.id));
      } else {
        //remove file from previous sample
        dispatch(removeFileFromSample(file.id, prevSampleIndex, prevPairIndex));
      }

      //add file to new pair in sample
      dispatch(addFileToNewPairInSample(file, sampleIndex));
    },
  });

  return (
    <Card
      title={sample.sampleName}
      extra={
        <Button
          onClick={removeSampleFromList}
          style={{ border: "none" }}
          icon={<IconRemove />}
        />
      }
    >
      {sample.pairs.map((pair, pairIndex) => (
        <SequencingRunSamplePair
          key={`sequencing-run-sample-pair-${pairIndex}`}
          pair={pair}
          pairIndex={pairIndex}
          sample={sample}
          sampleIndex={sampleIndex}
        />
      ))}

      <Row ref={dropOnNewPair} align="middle" justify="center">
        {isDropOnNewPairOver && (
          <>
            <Col flex="75px">
              <Skeleton.Avatar shape="circle" size={60} />
            </Col>
            <Col flex="auto">
              <Skeleton.Input size="large" block={true} />
            </Col>
          </>
        )}
        <Col span={24}>
          <Empty
            style={{ paddingTop: "10px" }}
            description={
              <Typography.Text type="secondary">
                {i18n("SequencingRunSample.empty")}
              </Typography.Text>
            }
            imageStyle={{ display: "none" }}
          />
        </Col>
      </Row>
    </Card>
  );
}

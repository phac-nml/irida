import React from "react";
import {
  addFileToExistingPairInSample,
  moveFileWithinSample,
  removeFile,
  removeFileFromSample,
  updateSample,
} from "../services/runReducer";
import { FONT_COLOR_PRIMARY } from "../../../styles/fonts";
import {
  IconSwap,
  IconSwapLeft,
  IconSwapRight,
} from "../../../components/icons/Icons";
import { SequencingRunFileCard } from "./SequencingRunFileCard";
import { useDrop } from "react-dnd";
import { Avatar, Button, Col, Row, Skeleton } from "antd";
import { useDispatch } from "react-redux";

/**
 * React component to render a sample pair of files.
 * @param {object} pair - the sample file pair
 * @param {number} pairIndex - the index of the file pair in the sample's pair array
 * @param {object} sample - the sample
 * @param {number} sampleIndex - the index of the sample in the sample array
 * @param {array} samples - list of samples
 * @returns {JSX.Element} - Returns a sample component
 */
export function SequencingRunSamplePair({
  pair,
  pairIndex,
  sample,
  sampleIndex,
  samples,
}) {
  const dispatch = useDispatch();

  const switchPair = () => {
    const updatedPairs = [...sample.pairs];
    updatedPairs[pairIndex] = { forward: pair.reverse, reverse: pair.forward };
    const updatedSample = {
      sampleName: sample.sampleName,
      pairs: updatedPairs,
    };
    dispatch(updateSample(updatedSample, sampleIndex));
  };

  const [{ isOver: isDropOnPairOver }, dropOnPair] = useDrop({
    accept: "card",
    collect: (monitor) => ({
      isOver: monitor.isOver(),
    }),
    canDrop: () => {
      //do not drop on a full pair
      if (pair.forward !== null && pair.reverse !== null) {
        return false;
      } else {
        return true;
      }
    },
    drop: (item) => {
      const { file, prevSampleIndex, prevPairIndex } = item;

      if (prevSampleIndex === sampleIndex) {
        //move file between pairs within the sample
        dispatch(
          moveFileWithinSample(file, sampleIndex, prevPairIndex, pairIndex)
        );
      } else {
        //remove file from source location
        if (prevSampleIndex === null) {
          //remove file from files list
          dispatch(removeFile(file.id));
        } else {
          //remove file from previous sample
          dispatch(
            removeFileFromSample(file.id, prevSampleIndex, prevPairIndex)
          );
        }
        //add file to target location
        dispatch(addFileToExistingPairInSample(file, sampleIndex, pairIndex));
      }
    },
  });

  return (
    <Row
      ref={dropOnPair}
      style={{ padding: "10px 0px" }}
      align="middle"
      justify="center"
    >
      {pair.forward !== null && (
        <>
          <Col flex="75px">
            <Avatar
              size={60}
              style={{ backgroundColor: FONT_COLOR_PRIMARY }}
              icon={<IconSwapRight />}
            />
          </Col>
          <Col flex="auto">
            <SequencingRunFileCard
              file={pair.forward}
              sampleIndex={sampleIndex}
              pairIndex={pairIndex}
            >
              {pair.forward.fileName}
            </SequencingRunFileCard>
          </Col>
        </>
      )}
      {pair.reverse !== null && (
        <>
          <Col span={2} offset={1}>
            <Button
              onClick={switchPair}
              style={{ border: "none" }}
              icon={<IconSwap />}
            />
          </Col>
          <Col flex="75px">
            <Avatar
              size={60}
              style={{ backgroundColor: FONT_COLOR_PRIMARY }}
              icon={<IconSwapLeft />}
            />
          </Col>
          <Col flex="auto">
            <SequencingRunFileCard
              file={pair.reverse}
              sampleIndex={sampleIndex}
              pairIndex={pairIndex}
            >
              {pair.reverse.fileName}
            </SequencingRunFileCard>
          </Col>
        </>
      )}
      {isDropOnPairOver && (
        <>
          {pair.forward === null && pair.reverse === null && (
            <>
              <Col flex="75px">
                <Skeleton.Avatar shape="circle" size={60} />
              </Col>
              <Col flex="auto">
                <Skeleton.Input size="large" block={true} />
              </Col>
            </>
          )}
          {pair.forward !== null && pair.reverse === null && (
            <>
              <Col span={2} offset={1}>
                <Skeleton.Button size="small" shape="circle" />
              </Col>
              <Col flex="75px">
                <Skeleton.Avatar shape="circle" size={60} />
              </Col>
              <Col flex="auto">
                <Skeleton.Input size="large" block={true} />
              </Col>
            </>
          )}
        </>
      )}
    </Row>
  );
}

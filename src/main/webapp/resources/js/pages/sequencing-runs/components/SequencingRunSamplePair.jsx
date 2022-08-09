import React from "react";
import {
  addFile,
  addFileToExistingPairInSample,
  moveFileWithinSample,
  removeFile,
  removeFileFromSample,
  updateSample,
} from "../services/runReducer";
import { FONT_COLOR_PRIMARY } from "../../../styles/fonts";
import {
  IconRemove,
  IconSwap,
  IconSwapLeft,
  IconSwapRight,
} from "../../../components/icons/Icons";
import { SequencingRunFileCard } from "./SequencingRunFileCard";
import { useDrop } from "react-dnd";
import { Avatar, Button, Col, Row, Skeleton } from "antd";
import { useDispatch } from "react-redux";
import { BORDERED_LIGHT } from "../../../styles/borders";

/**
 * React component to render a sample pair of files.
 * @param {object} pair - the sample file pair
 * @param {number} pairIndex - the index of the file pair in the sample's pair array
 * @param {object} sample - the sample
 * @param {number} sampleIndex - the index of the sample in the sample array
 * @returns {JSX.Element} - Returns a sample component
 */
export function SequencingRunSamplePair({
  pair,
  pairIndex,
  sample,
  sampleIndex,
}) {
  const dispatch = useDispatch();

  const clickSwitchPair = () => {
    const updatedPairs = [...sample.pairs];
    updatedPairs[pairIndex] = { forward: pair.reverse, reverse: pair.forward };
    const updatedSample = {
      sampleName: sample.sampleName,
      pairs: updatedPairs,
    };
    dispatch(updateSample(updatedSample, sampleIndex));
  };

  function clickRemoveFileFromList(fileId) {
    dispatch(removeFileFromSample(fileId, sampleIndex, pairIndex));
    dispatch(addFile(fileId));
  }

  const [{ isOver: isDropOnPairOver }, dropOnPair] = useDrop({
    accept: "card",
    collect: (monitor) => ({
      isOver: monitor.isOver(),
    }),
    canDrop: (item) => {
      if (pair.forward !== null && pair.reverse !== null) {
        //do not drop on an already full pair
        return false;
      } else if (pair.forward?.sequencingObjectType === "Fast5Object") {
        //do not create pair if forward is already fast5
        return false;
      } else if (
        pair.forward !== null &&
        item.file?.sequencingObjectType === "Fast5Object"
      ) {
        //do not create pair if reverse is to be fast5
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
      style={{ padding: "2.5px 0px" }}
      align="middle"
      justify="center"
      wrap={false}
    >
      {pair.forward !== null && (
        <Col flex="1" style={{ border: BORDERED_LIGHT }}>
          <Row align="middle" justify="center" wrap={false}>
            <Col flex="initial">
              <Avatar
                size="default"
                style={{ backgroundColor: FONT_COLOR_PRIMARY }}
                icon={<IconSwapRight />}
              />
            </Col>
            <Col flex="1">
              <SequencingRunFileCard
                file={pair.forward}
                sampleIndex={sampleIndex}
                pairIndex={pairIndex}
                style={{ border: "none" }}
              >
                {pair.forward.fileName}
              </SequencingRunFileCard>
            </Col>
            <Col flex="initial">
              <Button
                onClick={() => clickRemoveFileFromList(pair.forward.id)}
                style={{ border: "none" }}
                icon={<IconRemove />}
              />
            </Col>
          </Row>
        </Col>
      )}
      {pair.reverse !== null && (
        <>
          <Col span={2} offset={1}>
            <Button
              onClick={clickSwitchPair}
              style={{ border: "none" }}
              icon={<IconSwap />}
            />
          </Col>
          <Col flex="1" style={{ border: BORDERED_LIGHT }}>
            <Row align="middle" justify="center" wrap={false}>
              <Col flex="initial">
                <Avatar
                  size="default"
                  style={{ backgroundColor: FONT_COLOR_PRIMARY }}
                  icon={<IconSwapLeft />}
                />
              </Col>
              <Col flex="1">
                <SequencingRunFileCard
                  file={pair.reverse}
                  sampleIndex={sampleIndex}
                  pairIndex={pairIndex}
                  style={{ border: "none" }}
                >
                  {pair.reverse.fileName}
                </SequencingRunFileCard>
              </Col>
              <Col flex="initial">
                <Button
                  onClick={() => clickRemoveFileFromList(pair.reverse.id)}
                  style={{ border: "none" }}
                  icon={<IconRemove />}
                />
              </Col>
            </Row>
          </Col>
        </>
      )}
      {isDropOnPairOver && (
        <>
          {pair.forward === null && pair.reverse === null && (
            <>
              <Col flex="1">
                <Skeleton.Input size="default" block={true} />
              </Col>
            </>
          )}
          {pair.forward !== null && pair.reverse === null && (
            <>
              <Col span={2} offset={1}>
                <Skeleton.Button size="small" shape="circle" />
              </Col>
              <Col flex="1">
                <Skeleton.Input size="default" block={true} />
              </Col>
            </>
          )}
        </>
      )}
    </Row>
  );
}

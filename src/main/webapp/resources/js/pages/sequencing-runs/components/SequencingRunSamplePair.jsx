import React from "react";
import { removeFile, updateSample } from "../services/runReducer";
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
        //remove file from another pair within the sample
        const updatedPairs = [...sample.pairs];
        const prevPair = sample.pairs[prevPairIndex];

        updatedPairs[pairIndex] = {
          forward: pair.forward,
          reverse: file,
        };

        if (prevPair.reverse === null) {
          //the pair is going to be empty
          //removing it from the pair list
          updatedPairs.splice(prevPairIndex, 1);
        } else {
          //converting the pair into single
          updatedPairs[prevPairIndex] = {
            forward:
              prevPair.forward?.id === file.id
                ? prevPair.reverse
                : prevPair.forward,
            reverse: null,
          };
        }

        const updatedSample = {
          sampleName: sample.sampleName,
          pairs: updatedPairs,
        };
        dispatch(updateSample(updatedSample, prevSampleIndex));
      } else {
        //remove file from source location
        if (prevSampleIndex === null) {
          //remove file from files list
          dispatch(removeFile(file.id));
        } else {
          //remove file from previous sample
          const prevSample = samples[prevSampleIndex];
          const prevPairs = [...prevSample.pairs];
          const prevPair = prevSample.pairs[prevPairIndex];

          if (prevPair.reverse === null) {
            //the pair is going to be empty
            //removing it from the pair list
            prevPairs.splice(prevPairIndex, 1);
          } else {
            //converting the pair into single
            prevPairs[prevPairIndex] = {
              forward:
                prevPair.forward?.id === file.id
                  ? prevPair.reverse
                  : prevPair.forward,
              reverse: null,
            };
          }
          const updatedSample = {
            sampleName: prevSample.sampleName,
            pairs: prevPairs,
          };
          dispatch(updateSample(updatedSample, prevSampleIndex));
        }
        //add file to target location
        //assume the pair already has a forward file
        const updatedPairs = [...sample.pairs];
        updatedPairs[pairIndex] = { forward: pair.forward, reverse: file };
        const updatedSample = {
          sampleName: sample.sampleName,
          pairs: updatedPairs,
        };
        dispatch(updateSample(updatedSample, sampleIndex));
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

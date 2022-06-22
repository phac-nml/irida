import React from "react";
import { fetchPipelineSamples } from "../../apis/pipelines/pipelines";
import AutoSizer from "react-virtualized-auto-sizer";
import { VariableSizeList as VList } from "react-window";
import { notification, Space } from "antd";
import { useLaunch } from "./launch-context";
import { removeSample } from "../../apis/cart/cart";
import { SectionHeading } from "../../components/ant.design/SectionHeading";
import { SampleFilesListItem } from "./files/SampleFilesListItem";
import { setSelectedSampleFiles } from "./launch-dispatch";
import { grey3 } from "../../styles/colors";
import { BORDERED_LIGHT } from "../../styles/borders";

const DEFAULT_HEIGHT = 600;

/**
 * React component to display sample files that will be used in the launching
 * of the pipeline
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchFiles() {
  const listRef = React.useRef();
  const [selected, setSelected] = React.useState();
  const [height, setHeight] = React.useState(DEFAULT_HEIGHT);
  const [
    { acceptsPairedSequenceFiles: paired, acceptsSingleSequenceFiles: singles },
    dispatch,
  ] = useLaunch();

  /*
  State to hold the count of the number of samples currently being displayed by the UI.
   */
  const [samples, setSamples] = React.useState();

  /**
   * Calculate the height of the sample with its files.
   * @param index
   * @returns {number}
   */
  const getRowHeight = (index) => {
    if (samples[index].files.length) {
      return samples[index].files.length * 40 + 50;
    }
    return 100;
  };

  /*
  Called on initialization.  This gets the samples that are currently in the cart,
  with their associated files.
   */
  React.useEffect(() => {
    fetchPipelineSamples({
      paired,
      singles,
    })
      .then((data) => {
        const firstSelected = [];
        const firstSamples = [];
        data.forEach((sample) => {
          if (sample.files.length) {
            let defaultSequencingObjectAccepted = sample.files.filter(
              (sampleFile) => {
                if (
                  sample.defaultSequencingObject !== null &&
                  sampleFile.fileInfo.identifier ===
                    sample.defaultSequencingObject.identifier
                ) {
                  return true;
                }
              }
            );

            if (
              sample.defaultSequencingObject !== null &&
              defaultSequencingObjectAccepted.length > 0
            ) {
              sample.selected = sample.defaultSequencingObject.identifier;
            } else {
              sample.selected = sample.files[0].fileInfo.identifier;
            }
            firstSelected.push(sample.selected);
          }
          firstSamples.push(sample);
        });

        setSamples(firstSamples);
        setSelected(firstSelected);
      })
      .catch((message) => notification.error({ message }));
  }, [paired, singles]);

  /*
  Called when a user selects a different set of files to run on the sample.
  Needs to be updated through the reducer so that the correct file ids are
  set when the pipeline is launched.
   */
  React.useEffect(() => {
    setSelectedSampleFiles(dispatch, selected);
  }, [dispatch, selected]);

  /*
  Calculate the div size if the number of samples changes
   */
  React.useEffect(() => {
    if (samples) {
      let newHeight = 0;
      for (let i = 0; i < samples.length; i++) {
        newHeight += getRowHeight(i);
        if (newHeight > DEFAULT_HEIGHT) {
          newHeight = DEFAULT_HEIGHT;
          break;
        }
      }
      setHeight(newHeight + 2);
    }
  }, [samples]);

  /*
  Called independently for each sample when the selected file set is changed.
   */
  const updateSelectedFiles = (sample, current) => {
    const ids = new Set(selected);
    ids.delete(sample.selected);
    sample.selected = current;
    ids.add(current);
    setSelected(Array.from(ids));
  };

  /*
  Called to remove a sample from the cart.
   */
  const removeSampleFromCart = (sample, selectedId) => {
    removeSample(sample.project.id, sample.id).then(() => {
      // Find the index so that we can update the table heights
      const index = samples.findIndex((s) => s.id === sample.id);

      const updatedSamples = samples.filter((s) => s.id !== sample.id);
      if (updatedSamples.length) {
        setSamples(updatedSamples);
        const ids = new Set(selected);
        ids.delete(selectedId);
        setSelected(Array.from(ids));

        // Update the virtual list
        listRef.current.resetAfterIndex(index, true);
      }
    });
  };

  /**
   * Create the react component to render a sample with it's files.
   * @param index - index of the sample
   * @param style - passed from VirtualListComponent
   * @returns {JSX.Element}
   */
  const generateSample = ({ index, style }) => {
    const sample = samples[index];
    return (
      <SampleFilesListItem
        style={style}
        sample={sample}
        projectId={sample.project.id}
        removeSample={removeSampleFromCart}
        updateSelectedFiles={updateSelectedFiles}
      />
    );
  };

  return (
    <Space
      className="t-launch-files"
      direction="vertical"
      style={{ width: `100%` }}
    >
      <SectionHeading id="launch-files">
        {i18n("LaunchFiles.heading")}
      </SectionHeading>
      {samples ? (
        <div
          style={{
            height,
            width: "100%",
          }}
        >
          <AutoSizer>
            {({ height = DEFAULT_HEIGHT, width = 400 }) => (
              <VList
                style={{
                  border: BORDERED_LIGHT,
                  backgroundColor: grey3,
                }}
                ref={listRef}
                itemKey={(index) => samples[index].id}
                height={height}
                width={width}
                itemCount={samples.length}
                itemSize={getRowHeight}
              >
                {generateSample}
              </VList>
            )}
          </AutoSizer>
        </div>
      ) : null}
    </Space>
  );
}

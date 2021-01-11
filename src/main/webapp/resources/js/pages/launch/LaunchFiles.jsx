import React from "react";
import { fetchPipelineSamples } from "../../apis/pipelines/pipelines";
import AutoSizer from "react-virtualized-auto-sizer";
import { VariableSizeList as VList } from "react-window";
import { Button, Dropdown, Menu, notification, Space, Typography } from "antd";
import { useLaunch } from "./launch-context";
import { IconDropDown } from "../../components/icons/Icons";
import { removeSample } from "../../apis/cart/cart";
import { SectionHeading } from "../../components/ant.design/SectionHeading";
import { SampleFilesListItem } from "./files/SampleFilesListItem";
import { setSelectedSampleFiles } from "./launch-dispatch";
import { grey3, grey4 } from "../../styles/colors";

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
  const [
    { acceptsPairedSequenceFiles: paired, acceptsSingleSequenceFiles: singles },
    dispatch,
  ] = useLaunch();

  /*
  State to determine whether to hide samples that do not have any usable files
  in them.  These sample will not be run on the pipeline either way.
   */
  const [hideUnusable, setHideUnusable] = React.useState(true);
  /*
  State to hold the count of the number of samples currently being displayed by the UI.
   */
  const [visibleSamples, setVisibleSamples] = React.useState();
  const [samples, setSamples] = React.useState();

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
            sample.selected = sample.files[0].identifier;
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

  const toggleVisible = () => {
    if (hideUnusable) {
      setVisibleSamples(samples.filter((sample) => sample.files.length));
    } else {
      setVisibleSamples(samples);
    }
  };

  /*
  Called when there are samples or when the toggling the visible samples it triggered
  Sets the samples to display either all or only ones with good files.

   */
  React.useEffect(() => {
    if (samples) {
      toggleVisible();
    }
  }, [samples, hideUnusable]);

  /*
  Toggle whether or not to show samples that do not have files that
  can be run on the current pipeline.
   */
  const toggleUsable = () => setHideUnusable(!hideUnusable);

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
      const index = visibleSamples.findIndex((s) => s.id === sample.id);

      const updatedSamples = samples.filter((s) => s.id !== sample.id);
      if (updatedSamples.length) {
        setSamples(updatedSamples);
        const ids = new Set(selected);
        ids.delete(selectedId);
        setSelected(Array.from(ids));

        // Update the virtual list
        listRef.current.resetAfterIndex(index);
      }
    });
  };

  const generateSample = ({ index, style }) => {
    const sample = visibleSamples[index];
    return (
      <SampleFilesListItem
        style={style}
        sample={sample}
        removeSample={removeSampleFromCart}
        updateSelectedFiles={updateSelectedFiles}
      />
    );
  };

  const getRowHeight = (index) => visibleSamples[index].files.length * 40 + 50;

  return (
    <Space direction="vertical" style={{ width: `100%` }}>
      <SectionHeading id="launch-files">
        {i18n("LaunchFiles.heading")}
      </SectionHeading>
      <div
        style={{
          display: "flex",
          flexDirection: "row-reverse",
          alignItems: "center",
        }}
      >
        <Space>
          <Typography.Text type="secondary">
            {i18n(
              "LaunchFiles.showing",
              visibleSamples?.length,
              samples?.length
            )}
          </Typography.Text>
          <Dropdown
            overlay={
              <Menu>
                <Menu.Item onClick={toggleUsable}>
                  Toggle Usable Samples
                </Menu.Item>
              </Menu>
            }
          >
            <Button icon={<IconDropDown />} />
          </Dropdown>
        </Space>
      </div>
      {visibleSamples ? (
        <div
          style={{
            height: 500,
            width: "100%",
          }}
        >
          <AutoSizer>
            {({ height = 600, width = 400 }) => (
              <VList
                style={{
                  border: `1px solid ${grey4}`,
                  backgroundColor: grey3,
                }}
                ref={listRef}
                itemKey={(index) => visibleSamples[index].id}
                height={height}
                width={width}
                itemCount={visibleSamples.length}
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

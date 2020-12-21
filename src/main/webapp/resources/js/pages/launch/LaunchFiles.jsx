import React from "react";
import { fetchPipelineSamples } from "../../apis/pipelines/pipelines";
import {
  Button,
  Dropdown,
  List,
  Menu,
  notification,
  Space,
  Typography,
} from "antd";
import { useLaunch } from "./launch-context";
import { IconDropDown } from "../../components/icons/Icons";
import { removeSample } from "../../apis/cart/cart";
import { SectionHeading } from "../../components/ant.design/SectionHeading";
import { SampleFilesListItem } from "./files/SampleFilesListItem";
import { setSelectedSampleFiles } from "./launch-dispatch";

/**
 * React component to display sample files that will be used in the launching
 * of the pipeline
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchFiles() {
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
        setSamples(data);
        setSelected(
          data
            .filter((sample) => sample.files.length)
            .map((sample) => sample.files[0].identifier)
        );
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
  Called when there are samples or when the toggling the visible samples it triggered
  Sets the samples to display either all or only ones with good files.

   */
  React.useEffect(() => {
    if (samples) {
      if (hideUnusable) {
        setVisibleSamples(samples.filter((sample) => sample.files.length));
      } else {
        setVisibleSamples(samples);
      }
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
  const updateSelectedFiles = (previous, current) => {
    const ids = new Set(selected);
    ids.delete(previous);
    ids.add(current);
    setSelected(Array.from(ids));
  };

  /*
  Called to remove a sample from the cart.
   */
  const removeSampleFromCart = (sample, selectedId) => {
    removeSample(sample.project.id, sample.id).then(() => {
      setSamples(samples.filter((sample) => sample.id !== sample.id));
      const ids = new Set(selected);
      ids.delete(selectedId);
      setSelected(Array.from(ids));
    });
  };

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
      <List
        id="launch-files"
        bordered
        dataSource={visibleSamples}
        renderItem={(sample) => (
          <SampleFilesListItem
            key={`list-${sample.label}`}
            sample={sample}
            removeSample={removeSampleFromCart}
            updateSelectedFiles={updateSelectedFiles}
          />
        )}
      />
    </Space>
  );
}

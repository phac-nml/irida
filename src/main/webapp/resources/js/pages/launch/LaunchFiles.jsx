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
  const [hideUnusable, setHideUnusable] = React.useState(true);
  const [visibleSamples, setVisibleSamples] = React.useState();
  const [samples, setSamples] = React.useState();

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

  React.useEffect(() => {
    setSelectedSampleFiles(dispatch, selected);
  }, [selected]);

  React.useEffect(() => {
    if (samples) {
      if (hideUnusable) {
        setVisibleSamples(samples.filter((s) => s.files.length));
      } else {
        setVisibleSamples(samples);
      }
    }
  }, [samples, hideUnusable]);

  const toggleUsable = () => setHideUnusable(!hideUnusable);

  const updateSelectedFiles = (previous, current) => {
    const ids = new Set(selected);
    ids.delete(previous);
    ids.add(current);
    setSelected(Array.from(ids));
  };

  const removeSampleFromCart = (sample, selectedId) => {
    removeSample(sample.project.id, sample.id).then(() => {
      setSamples(samples.filter((s) => sample.id !== s.id));
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

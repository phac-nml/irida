import React from "react";
import { fetchPipelineSamples } from "../../apis/pipelines/pipelines";
import { Button, Dropdown, List, Menu, Radio, Space, Tooltip } from "antd";
import { SampleDetailSidebar } from "../../components/samples/SampleDetailSidebar";
import { useLaunch } from "./launch-context";
import { grey2 } from "../../styles/colors";
import { IconDropDown } from "../../components/icons/Icons";
import styled from "styled-components";
import { removeSample } from "../../apis/cart/cart";

const BlockRadio = styled(Radio)`
  display: block;
  height: 30px;
  line-height: 30px;
  font-weight: normal;
`;

export function LaunchFiles() {
  const [
    { acceptsPairedSequenceFiles: paired, acceptsSingleSequenceFiles: singles },
  ] = useLaunch();
  const [hideUnusable, setHideUnusable] = React.useState(true);
  const [visibleSamples, setVisibleSamples] = React.useState();
  const [samples, setSamples] = React.useState();

  React.useEffect(() => {
    fetchPipelineSamples({
      paired,
      singles,
    }).then(setSamples);
  }, [paired, singles]);

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

  const removeSampleFromCart = (sample) => {
    removeSample(sample.project.id, sample.id).then(() => {
      setSamples(samples.filter((s) => sample.id !== s.id));
    });
  };

  return (
    <Space direction="vertical" style={{ width: `100%` }}>
      <div style={{ display: "flex", flexDirection: "row-reverse" }}>
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
      </div>
      <List
        bordered
        dataSource={visibleSamples}
        renderItem={(sample) => (
          <List.Item
            actions={[
              <Button
                type="text"
                key={`remove`}
                onClick={() => removeSampleFromCart(sample)}
              >
                Remove
              </Button>,
            ]}
            style={{
              backgroundColor: sample.files.length ? "transparent" : grey2,
            }}
          >
            <List.Item.Meta
              title={
                <SampleDetailSidebar sampleId={sample.id}>
                  <Button size="small">{sample.label}</Button>
                </SampleDetailSidebar>
              }
              description={
                sample.files.length ? (
                  <div style={{ overflowX: "auto" }}>
                    <Radio.Group>
                      {sample.files.map((file) => (
                        <BlockRadio key={`file-${file.id}`}>
                          <Tooltip title={file.label}>{file.label}</Tooltip>
                        </BlockRadio>
                      ))}
                    </Radio.Group>
                  </div>
                ) : (
                  <span>YOU NEED SOME FILES YO!</span>
                )
              }
            />
          </List.Item>
        )}
      />
    </Space>
  );
}

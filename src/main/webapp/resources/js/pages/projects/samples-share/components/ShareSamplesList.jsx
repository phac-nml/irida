import { Alert, Button, List, Space, Switch, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";

import { FixedSizeList as VList } from "react-window";
import { useGetProjectDetailsQuery } from "../../../../apis/projects/project";
import { useGetCommonSampleIdentifiersQuery } from "../../../../apis/projects/projects";
import {
  IconArrowLeft,
  IconArrowRight,
  IconCheck,
  IconLocked,
} from "../../../../components/icons/Icons";
import { SampleDetailViewer } from "../../../../components/samples/SampleDetailViewer";
import {
  removeSample,
  setNextStep,
  setPreviousStep,
  setSamplesLockedStatus,
} from "../services/shareSlice";
import { ShareStatusAvatar } from "./ShareStatusAvatar";

export function ShareSamplesList({ projectId }) {
  const dispatch = useDispatch();
  const { locked, destination = {}, samples } = useSelector(
    (state) => state.reducer
  );

  const { data: projectDetails = {}, isLoading } = useGetProjectDetailsQuery(
    projectId
  );

  const { data: commonSampleIds = [] } = useGetCommonSampleIdentifiersQuery(
    { projectId: destination.identifier, sampleIds: samples.map((s) => s.id) },
    {
      skip: destination.identifier === undefined,
    }
  );

  const removeSampleByIndex = (index) => dispatch(removeSample(index));

  const Row = ({ index, style }) => {
    const sample = samples[index];

    return (
      <List.Item
        style={style}
        actions={[
          <Button
            key="remove"
            type="link"
            onClick={() => removeSampleByIndex(index)}
          >
            remove
          </Button>,
        ]}
      >
        <List.Item.Meta
          avatar={
            <ShareStatusAvatar
              locked={sample.locked}
              remote={projectDetails?.remote}
            />
          }
          title={
            <SampleDetailViewer sampleId={sample.id}>
              <Button size={"small"}>{sample.name}</Button>
            </SampleDetailViewer>
          }
          description={
            commonSampleIds.includes(sample.id)
              ? `Exists in ${destination.name}  and will not be recopied`
              : null
          }
        />
      </List.Item>
    );
  };

  const updateLockedStatus = (checked) =>
    dispatch(setSamplesLockedStatus(checked));

  const getLockedMessage = (locked) =>
    locked
      ? "Samples will be locked from modification."
      : "Allow modification of samples in destination project";

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      {!isLoading && projectDetails.remote && (
        <Alert
          message={`${projectDetails.label} is a remote sync project`}
          description={i18n("project.samples.modal.copy.remote")}
          type="info"
          showIcon
          icon={<IconLocked />}
        />
      )}
      <List bordered rowKey={(item) => item.name}>
        <VList
          height={600}
          itemCount={samples.length}
          itemSize={75}
          width={`100%`}
        >
          {Row}
        </VList>
      </List>
      {!isLoading && !projectDetails.remote ? (
        <Space>
          <Switch
            checkedChildren={<IconLocked />}
            unCheckedChildren={<IconCheck />}
            checked={locked}
            onChange={updateLockedStatus}
          />
          <Typography.Text strong>{getLockedMessage(locked)}</Typography.Text>
        </Space>
      ) : null}
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <Button onClick={() => dispatch(setPreviousStep())}>
          <Space>
            <IconArrowLeft />
            <span>Select a Project</span>
          </Space>
        </Button>
        <Button onClick={() => dispatch(setNextStep())}>
          <Space>
            <span>Check Metadata Field Permissions</span>
            <IconArrowRight />
          </Space>
        </Button>
      </div>
    </Space>
  );
}

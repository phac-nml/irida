import React from "react";
import { Alert, Col, Divider, List, Modal, Row, Typography } from "antd";
import { useRemoveMutation } from "../../../../apis/projects/samples";
import LockedSamplesList from "./LockedSamplesList";
import AssociatedSamplesList from "./AssociatedSamplesList";

/**
 * React Element to display a modal with sample to be removed from the current
 * project.
 *  - Will display samples that are locked - cannot be removed.
 *  - Will display associated samples that cannot be removed from this project.
 * @param {array} samples - list of samples to remove from the current project
 * @param {boolean} visible - whether the modal is currently visible on the page
 * @param {function} onComplete - action to perform after the remove is complete
 * @param {function} onCancel - action to perform if the remove is cancelled.
 * @returns {JSX.Element}
 * @constructor
 */
export default function RemoveModal({
  samples,
  visible,
  onComplete,
  onCancel,
}) {
  const [removeSamples, { isLoading, error }] = useRemoveMutation();

  const onOk = async () => {
    try {
      await removeSamples(samples.valid.map((sample) => sample.id));
      onComplete();
    } catch (e) {
      // Do nothing, handled by mutation
    }
  };

  return (
    <Modal
      title={i18n("RemoveModal.title")}
      className="t-remove-modal"
      visible={visible}
      onCancel={onCancel}
      onOk={onOk}
      okText={i18n("RemoveModal.okText")}
      okButtonProps={{
        loading: isLoading,
      }}
      width={600}
    >
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <List
            size="small"
            bordered
            header={<Typography.Text>Samples to be removed</Typography.Text>}
            dataSource={samples.valid}
            renderItem={(sample) => (
              <List.Item>
                <List.Item.Meta title={sample.sampleName} />
              </List.Item>
            )}
          />
        </Col>
        {(samples.locked.length > 0 || samples.associated.length > 0) && (
          <Col span={24}>
            <Divider orientation="left" plain>
              {i18n("RemoveModal.divider")}
            </Divider>
          </Col>
        )}
        {samples.locked.length > 0 && (
          <Col span={24}>
            <LockedSamplesList locked={samples.locked} />
          </Col>
        )}
        {samples.associated.length > 0 && (
          <Col span={24}>
            <AssociatedSamplesList associated={samples.associated} />
          </Col>
        )}
        {error && (
          <Alert type="error" showIcon message={i18n("RemoveModal.error")} />
        )}
      </Row>
    </Modal>
  );
}

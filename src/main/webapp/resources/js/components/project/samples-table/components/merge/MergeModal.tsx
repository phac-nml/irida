import React, { useCallback, useEffect, useState } from "react";

import {
  Alert,
  Checkbox,
  Col,
  Form,
  Input,
  Modal,
  notification,
  Radio,
  Row,
  Space,
  Typography,
} from "antd";
import type { Samples } from "./MergeTrigger";
import { useParams } from "react-router-dom";
import { useMergeSamplesMutation } from "../../../../../redux/endpoints/project-samples";
import LockedSamplesList from "../LockedSamplesList";
import { serverValidateSampleName } from "../../../../../utilities/validation-utilities";
import { useProjectSamples } from "../../useProjectSamplesContext";

/**
 * React element to display a modal to merge multiple samples into a single one.
 * @constructor
 */
export default function MergeModal({
  visible,
  samples,
  hideModal,
}: {
  visible: boolean;
  samples: NonNullable<Samples>;
  hideModal: () => void;
}): JSX.Element {
  const { dispatch } = useProjectSamples();
  const [unlocked, locked] = samples;
  const { projectId } = useParams();

  const [merge, { isLoading }] = useMergeSamplesMutation();
  const [renameSample, setRenameSample] = useState(false);

  const [error, setError] = useState(undefined);
  const [form] = Form.useForm();

  const initialValues = {
    primary: unlocked[0]?.id,
    newName: "",
  };

  useEffect(() => {
    if (!renameSample) {
      form.setFieldsValue({
        newName: "",
      });
    }
  }, [form, renameSample]);

  // Server validate new name
  const validateName = async (name: string): Promise<string | void> => {
    if (renameSample) {
      return serverValidateSampleName(name);
    } else {
      return Promise.resolve();
    }
  };

  const onSubmit = useCallback(async () => {
    let values;

    try {
      values = await form.validateFields();
    } catch {
      /*
      If the form is in an invalid state it will hit here.  This will prevent the
      invalid date from being submitted and display the errors (if not already displayed)
       to the user.
       */
      return;
    }
    const ids = unlocked
      .map((sample) => sample.id)
      .filter((id) => id !== values.primary);

    const { message } = await merge({
      projectId,
      body: {
        ...values,
        ids,
      },
    }).unwrap();

    notification.success({
      message: i18n("MergeModal.success"),
      description: message,
    });
    dispatch({ type: `deselectAllSamples` });
    hideModal();
  }, [dispatch, form, hideModal, merge, projectId, unlocked]);

  const clearError = useCallback(() => setError(undefined), []);
  const toggleRenameSample = useCallback(
    (e) => setRenameSample(e.target.checked),
    []
  );

  return (
    <Modal
      visible={visible}
      title={i18n("MergeModal.title")}
      className="t-merge-modal"
      onCancel={hideModal}
      okText={i18n("MergeModal.okText")}
      okButtonProps={{
        loading: isLoading,
      }}
      onOk={onSubmit}
      cancelText={i18n("MergeModal.cancelText")}
      width={600}
    >
      <Row gutter={[16, 16]}>
        {unlocked.length >= 2 ? (
          <>
            <Col span={24}>
              <Alert
                type="warning"
                showIcon
                message={i18n("MergeModal.metadata-warning")}
              />
            </Col>
            {error !== undefined ? (
              <Col span={24}>
                <Alert
                  showIcon
                  type="error"
                  message={error}
                  closable
                  onClose={clearError}
                />
              </Col>
            ) : null}
            <Col span={24}>
              <Form form={form} layout="vertical" initialValues={initialValues}>
                <Form.Item
                  name="primary"
                  label={i18n("MergeModal.input-primary")}
                  required
                >
                  <Radio.Group>
                    <Space direction="vertical">
                      {unlocked.map((sample) => {
                        return (
                          <Radio value={sample.id} key={`sample-${sample.id}`}>
                            {sample.sampleName}
                          </Radio>
                        );
                      })}
                    </Space>
                  </Radio.Group>
                </Form.Item>
                <Form.Item noStyle>
                  <Checkbox
                    className="t-custom-checkbox"
                    checked={renameSample}
                    onChange={toggleRenameSample}
                  >
                    {i18n("MergeModal.rename")}
                  </Checkbox>
                  <Form.Item
                    name="newName"
                    rules={[
                      () => ({
                        validator(_, value) {
                          return validateName(value);
                        },
                      }),
                    ]}
                  >
                    <Input disabled={!renameSample} />
                  </Form.Item>
                </Form.Item>
              </Form>
            </Col>
          </>
        ) : (
          <Col span={24}>
            <Alert
              type="error"
              showIcon
              message={i18n("MergeModal.error-valid")}
            />
          </Col>
        )}
        {locked.length ? (
          <Col span={24}>
            <Typography.Text strong>
              {i18n("LockedSamplesList.header")}
            </Typography.Text>
            <LockedSamplesList locked={locked} />
          </Col>
        ) : null}
      </Row>
    </Modal>
  );
}

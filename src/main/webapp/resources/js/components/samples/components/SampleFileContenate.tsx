import {
  Avatar,
  Form,
  Input,
  List,
  Modal,
  notification,
  Radio,
  Space,
  Typography,
} from "antd";
import React from "react";
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";
import { useResetFormOnCloseModal } from "../../../hooks";
import {
  SampleConcatenationObject,
  SequencingFile,
  SequencingObject,
  useConcatenateSequencingObjectsMutation,
} from "../../../apis/samples/samples";
import {
  addToSequenceFiles,
  removeFileObjectFromSample,
  resetConcatenateSelected,
} from "../sampleFilesSlice";
import { setDefaultSequencingObject } from "../sampleSlice";
import { IconArrowLeft, IconArrowRight, IconFile } from "../../icons/Icons";
import { LoadingOutlined } from "@ant-design/icons";
import { ErrorAlert } from "../../../components/alerts/ErrorAlert";

const { Title } = Typography;

export interface SampleFileConcatenateProps {
  children: React.ReactElement;
}

/**
 * Function to render Sequencing Object concatenation modal
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFileConcatenate({
  children,
}: SampleFileConcatenateProps): JSX.Element {
  const [visible, setVisible] = React.useState<boolean>(false);
  const dispatch = useAppDispatch();
  const [form] = Form.useForm();
  const { sample } = useAppSelector((state) => state.sampleReducer);
  const { concatenateSelected } = useAppSelector(
    (state) => state.sampleFilesReducer
  );
  const [concatenateSeqObjectFiles] = useConcatenateSequencingObjectsMutation();
  const [concatenateButtonDisabled, setConcatenateButtonDisabled] =
    React.useState<boolean>(true);
  const [concatenating, setConcatenating] = React.useState<boolean>(false);
  const [concatenationError, setConcatenationError] =
    React.useState<string>("");

  useResetFormOnCloseModal({
    form,
    visible,
  });

  const concatenateFiles = () => {
    setConcatenationError("");
    const sequencingObjectIds = concatenateSelected.map(
      (seqObject: SequencingObject) => seqObject.identifier
    );

    form.validateFields().then((values) => {
      setConcatenating(true);
      concatenateSeqObjectFiles({
        sampleId: sample.identifier,
        sequencingObjectIds: sequencingObjectIds,
        newFileName: values.new_file_name,
        removeOriginals: values.remove_original_files,
      })
        .unwrap()
        .then((data: SampleConcatenationObject[]) => {
          let message = i18n("SampleFilesConcatenate.concatenationSuccess");

          if (values.remove_original_files) {
            message = i18n("SampleFilesConcatenate.concatenationRemoveSuccess");

            /*
            Remove from the state the sequencing objects that were used to concatenate the files
             */
            sequencingObjectIds.forEach((seqObjId: number) => {
              dispatch(
                removeFileObjectFromSample({
                  fileObjectId: seqObjId,
                  type: "sequencingObject",
                })
              );

              /*
              If the sample default sequencing object was removed then update defaultSequencingObject
              in the state
               */
              if (
                sample.defaultSequencingObject !== null &&
                seqObjId === sample.defaultSequencingObject.identifier
              ) {
                dispatch(setDefaultSequencingObject(null));
              }
            });
          }

          dispatch(resetConcatenateSelected());
          dispatch(
            addToSequenceFiles({
              sequenceFiles: data.sampleSequencingObjectFileModels,
            })
          );
          notification.success({ message });
          form.resetFields();
          setVisible(false);
        })
        .catch((error) => {
          setConcatenating(false);
          setConcatenationError(error.data.concatenationError);
        });
    });
  };

  /*
   Check if files selected for concatenation
   are of the same type or not
   */
  const validateFileTypes = () => {
    const numFilesSelected = concatenateSelected.length;
    let pairedCount = 0;
    let singleEndCount = 0;

    concatenateSelected.forEach(
      (selected: { files: SequencingFile[]; sequenceFile: SequencingFile }) => {
        if (selected.files !== undefined) {
          pairedCount++;
        } else if (selected.sequenceFile !== undefined) {
          singleEndCount++;
        }
      }
    );

    if (pairedCount === numFilesSelected) return true;
    if (singleEndCount === numFilesSelected) return true;
    return false;
  };

  const layoutProps = {
    layout: "vertical",
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => {
          const fileTypesValid = validateFileTypes();
          if (fileTypesValid) {
            setVisible(true);
          } else {
            notification.warning({
              message: i18n("SampleFilesConcatenate.mixedFileTypesWarning"),
              duration: 10,
            });
            dispatch(resetConcatenateSelected());
          }
        },
      })}
      {visible ? (
        <Modal
          className="t-concatenate-confirm-modal"
          onCancel={() => {
            dispatch(resetConcatenateSelected());
            setConcatenationError("");
            setConcatenateButtonDisabled(true);
            setVisible(false);
          }}
          visible={visible}
          onOk={concatenateFiles}
          okText={i18n("SampleFilesConcatenate.okText")}
          cancelText={i18n("SampleFilesConcatenate.cancelText")}
          okButtonProps={{
            className: "t-concatenate-confirm",
            disabled: concatenateButtonDisabled,
            icon: concatenating && <LoadingOutlined />,
          }}
          cancelButtonProps={{ className: "t-concatenate-cancel" }}
        >
          <Space size="large" direction="vertical" style={{ width: `100%` }}>
            <Title level={4}>{i18n("SampleFilesConcatenate.title")}</Title>

            {concatenationError !== "" && (
              <ErrorAlert message={concatenationError} />
            )}
            <List
              bordered
              {...layoutProps}
              dataSource={concatenateSelected}
              renderItem={(seqObject: SequencingObject) => {
                if (seqObject.files !== undefined) {
                  return (
                    <>
                      <List.Item
                        key={`seqObject-${seqObject.forwardSequenceFile.identifier}`}
                        style={{ width: `100%` }}
                        className="t-pair-forward-sequence-file"
                      >
                        <List.Item.Meta
                          avatar={
                            <Avatar size={`small`} icon={<IconArrowRight />} />
                          }
                          title={seqObject.forwardSequenceFile.label}
                        />
                      </List.Item>
                      <List.Item
                        key={`seqObject-${seqObject.reverseSequenceFile.identifier}`}
                        style={{ width: `100%` }}
                        className="t-pair-reverse-sequence-file"
                      >
                        <List.Item.Meta
                          avatar={
                            <Avatar size={`small`} icon={<IconArrowLeft />} />
                          }
                          title={seqObject.reverseSequenceFile.label}
                        />
                      </List.Item>
                    </>
                  );
                } else {
                  return (
                    <List.Item
                      key={`seqObject-${seqObject.identifier}`}
                      style={{ width: `100%` }}
                      className="t-single-end-file"
                    >
                      <List.Item.Meta
                        avatar={<Avatar size={`small`} icon={<IconFile />} />}
                        title={seqObject.label}
                      />
                    </List.Item>
                  );
                }
              }}
            />

            <Form
              layout="vertical"
              form={form}
              onFieldsChange={() => {
                const nameValue = form.getFieldValue("new_file_name");
                if (nameValue.length >= 3) {
                  setConcatenateButtonDisabled(false);
                } else {
                  setConcatenateButtonDisabled(true);
                }
              }}
            >
              <Form.Item
                key="new-file-name"
                name="new_file_name"
                label={i18n("SampleFilesConcatenate.newFileName")}
                rules={[
                  {
                    required: true,
                    message: (
                      <div className="t-new-file-name-required">
                        {i18n("SampleFilesConcatenate.nameValidationError")}
                      </div>
                    ),
                  },
                  {
                    min: 3,
                    message: (
                      <div className="t-name-min-length">
                        {i18n(
                          "SampleFilesConcatenate.nameLengthValidationError"
                        )}
                      </div>
                    ),
                  },
                ]}
              >
                <Input id="t-concat-new-file-name" />
              </Form.Item>

              <Form.Item
                key="remove-original-files"
                name="remove_original_files"
                label={i18n("SampleFilesConcatenate.removeOriginalFiles")}
                initialValue={false}
              >
                <Radio.Group optionType="button" buttonStyle="solid">
                  <div
                    style={{ display: "flex", justifyContent: "space-between" }}
                  >
                    <Radio.Button
                      value={false}
                      className="t-remove-originals-false"
                    >
                      {i18n("SampleFilesConcatenate.keepOriginals")}
                    </Radio.Button>
                    <Radio.Button
                      value={true}
                      className="t-remove-originals-true"
                    >
                      {i18n("SampleFilesConcatenate.removeOriginals")}
                    </Radio.Button>
                  </div>
                </Radio.Group>
              </Form.Item>
            </Form>
          </Space>
        </Modal>
      ) : null}
    </>
  );
}

import React, { useEffect, useState } from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import { message, Modal, Upload } from "antd";
import { IconCloudUpload } from "../icons/Icons";
import { Router } from "@reach/router";

const { Dragger } = Upload;

function UploadReferenceFile({ afterReferenceUpload }) {
  const options = {
    multiple: true,
    action: setBaseUrl(`referenceFiles/new`),
    onChange(info) {
      const { status } = info.file;
      if (status !== "uploading") {
        // console.log("NOT UPLOADING", info.file, info.fileList);
      }
      if (status === "done") {
        // console.log(`DONE`, info);
        message.success(`${info.file.name} file uploaded successfully.`);
        afterReferenceUpload({
          name: info.file.name,
          id: info.file.response["uploaded-file-id"],
        });
      } else if (status === "error") {
        message.error(`${info.file.name} file upload failed.`);
      }
    },
  };
  return (
    <Dragger {...options}>
      <p className="ant-upload-drag-icon">
        <IconCloudUpload />
      </p>
      <p className="ant-upload-text">
        Click or drag file to this area to upload
      </p>
      <p className="ant-upload-hint">
        Support for a single or bulk upload. Strictly prohibit from uploading
        company data or other band files
      </p>
    </Dragger>
  );
}

export default function PipelineDetailsModal({
  visible,
  id,
  onCancel,
  automated,
}) {
  const [details, setDetails] = useState({});
  const [referenceFiles, setReferenceFiles] = useState([]);
  const [reference, setReference] = useState(undefined);

  const afterReferenceUpload = (file) => {
    const t = [...referenceFiles];
    t.push(file);
    setReferenceFiles(t);
  };

  useEffect(() => {
    console.log(referenceFiles.length);
    setReference(referenceFiles.length ? referenceFiles[0].id : "");
  }, [referenceFiles]);

  const updateName = (event) => setName(event.currentTarget.value);

  return (
    <Modal
      title={i18n("PipelineDetailsModal.title", details.name)}
      visible={visible}
      onCancel={onCancel}
      width={800}
    >
      <Router>
        <h1>FUCK</h1>
        {/*  {typeof reference !== "undefined" ? (*/}
        {/*    <>*/}
        {/*      <Form.Item label={`REFERENCE FILES`}>*/}
        {/*        <Select*/}
        {/*          defaultValue={reference}*/}
        {/*          disabled={!referenceFiles.length}*/}
        {/*        >*/}
        {/*          {referenceFiles.map((file) => (*/}
        {/*            <Select.Option key={file.id} value={file.id}>*/}
        {/*              {file.name}*/}
        {/*            </Select.Option>*/}
        {/*          ))}*/}
        {/*        </Select>*/}
        {/*      </Form.Item>*/}
        {/*      <Form.Item>*/}
        {/*        <UploadReferenceFile*/}
        {/*          afterReferenceUpload={afterReferenceUpload}*/}
        {/*        />*/}
        {/*      </Form.Item>*/}
        {/*    </>*/}
        {/*  ) : null}*/}
        {/*  {details.canPipelineWriteToSamples ? (*/}
        {/*    <Form.Item help={"Need to figure out help text"}>*/}
        {/*      <Checkbox>Save results to sample</Checkbox>*/}
        {/*    </Form.Item>*/}
        {/*  ) : null}*/}
        {/*  <Divider />*/}
        {/*</Form>*/}
      </Router>
    </Modal>
  );
}

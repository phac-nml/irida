import React, { useState } from "react";
import { Button, Modal, Upload } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";

export function SampleFileUploader() {
  const [visible, setVisible] = useState(false);
  return (
    <>
      <Button onClick={() => setVisible(true)}>
        {i18n("samples.files.upload.btn")}
      </Button>
      <Modal
        title={"UPLOAD FILES"}
        visible={visible}
        onCancel={() => setVisible(false)}
      >
        <Upload
          action={setBaseUrl(`/samples/${window.PAGE.id}/sequenceFiles/upload`)}
          multiple={true}
          name="files"
          directory={true}
        >
          <Button>UPLOAD SEQUENCE FILES</Button>
        </Upload>
      </Modal>
    </>
  );
}

import React, { useEffect, useState } from "react";
import { render } from "react-dom";
import { Button, Input, Modal, Space, Upload } from "antd";
import { IconPlusCircle } from "../../../../components/icons/Icons";
import { grey9 } from "../../../../styles/colors";
import { Link, navigate, Router } from "@reach/router";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { useModalBackButton } from "../../../../hooks";

function AddSampleStepOne({ path }) {
  return (
    <>
      <Space direction={"vertical"} style={{ width: "100%" }}>
        <Button block>
          <Link to={setBaseUrl(`${path}/upload`)}>With Sequence Files</Link>
        </Button>
        <Button block>Without Sequence Files</Button>
      </Space>
    </>
  );
}

function UploadSequenceFiles() {
  const [name, setName] = useState();

  const onFileSelect = ({ file, fileList }) => {
    const [filename] = file.name.split(".");
    setName(filename);
  };

  const updateName = (e) => {
    setName(e.target.value);
    // Need to do validation here!!!
  };

  return (
    <>
      <Upload onChange={onFileSelect}>
        <Button>Click to upload files</Button>
      </Upload>
      <Input value={name} onChange={updateName} />
    </>
  );
}

function AddSample() {
  const [visible, setVisible] = useState(false);
  const location = setBaseUrl(`/projects/${window.project.id}/add-sample`);

  useEffect(() => {
    if (window.location.href.includes("add-sample")) {
      setVisible(true);
    }
  }, []);

  useModalBackButton(() => setVisible(false));

  const openNewSampleModal = () => {
    window.history.pushState({}, null, location);
    setVisible(true);
  };

  const closeNewSampleModal = () => {
    // Need to update the url to the original one.
    navigate(setBaseUrl(`/projects/${window.project.id}`));
    setVisible(false);
  };

  return (
    <>
      <Button
        type="link"
        style={{
          margin: `0 inherit`,
          padding: 0,
          paddingLeft: 20,
          color: grey9,
        }}
        icon={<IconPlusCircle style={{ marginRight: 3 }} />}
        onClick={openNewSampleModal}
      >
        {i18n("project.samples.nav.new")}
      </Button>
      <Modal
        visible={visible}
        onCancel={closeNewSampleModal}
        title={"ADD NEW SAMPLE"}
      >
        <Router>
          <AddSampleStepOne path={location} default />
          <UploadSequenceFiles path={`${location}/upload`} />
        </Router>
      </Modal>
    </>
  );
}

render(<AddSample />, document.querySelector(".js-add-sample"));

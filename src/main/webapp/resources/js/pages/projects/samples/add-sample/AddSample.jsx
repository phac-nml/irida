import React, { useCallback, useEffect, useRef, useState } from "react";
import { render } from "react-dom";
import { Button, Form, Input, Modal } from "antd";
import { IconPlusCircle } from "../../../../components/icons/Icons";
import { grey6, grey9 } from "../../../../styles/colors";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { OntologySelect } from "../../../../components/ontology";
import { TAXONOMY } from "../../../../apis/ontology/taxonomy";
import { useDebounce, useModalBackButton } from "../../../../hooks";

function AddSampleForm({ onSubmitForm }) {
  const [name, setName] = useState("");
  const [nameHelp, setNameHelp] = useState("");
  const [nameStatus, setNameStatus] = useState(undefined);
  const debouncedName = useDebounce(name);
  const [organism, setOrganism] = useState("");
  const nameRef = useRef();

  const validateName = useCallback(() => {
    return fetch(
      setBaseUrl(`/projects/${window.project.id}/samples/add-sample/validate`),
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name: debouncedName }),
      }
    ).then((response) => response.json());
  }, [debouncedName]);

  useEffect(() => {
    nameRef.current.focus();
  }, []);

  useEffect(() => {
    if (debouncedName.length) {
      setNameStatus("validating");
      validateName().then((response) => {
        setNameHelp(response.help);
        setNameStatus(response.status);
      });
    }
  }, [debouncedName, validateName]);

  const updateName = (e) => {
    setName(e.target.value);
  };

  const updateOrganism = (value) => {
    setOrganism(value);
    console.log(value);
  };

  return (
    <Form layout={"vertical"}>
      <Form.Item
        label={"Sample Name"}
        hasFeedback={nameHelp.length}
        validateStatus={nameStatus}
        help={nameHelp}
      >
        <Input ref={nameRef} value={name} onChange={updateName} />
      </Form.Item>
      <Form.Item label={"Organism"}>
        <OntologySelect
          term={organism}
          ontology={TAXONOMY}
          onTermSelected={updateOrganism}
        />
      </Form.Item>
      <Form.Item>
        <Button type={"primary"} htmlType={"submit"}>
          SUBMIT
        </Button>
      </Form.Item>
    </Form>
  );
}

function AddSample() {
  const [visible, setVisible] = useState(false);
  const location = setBaseUrl(`/projects/${window.project.id}/add-sample`);

  const openNewSampleModal = () => {
    // Allow the user to use the back button.
    window.history.pushState({}, null, location);
    setVisible(true);
  };

  const closeNewSampleModal = () => {
    // Need to update the url to the original one.
    window.history.pushState(
      {},
      null,
      setBaseUrl(`/projects/${window.project.id}`)
    );
    setVisible(false);
  };

  useModalBackButton(openNewSampleModal, closeNewSampleModal, "add-sample");

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
        icon={
          <IconPlusCircle style={{ marginRight: 3 }} twoToneColor={grey6} />
        }
        onClick={openNewSampleModal}
      >
        {i18n("project.samples.nav.new")}
      </Button>
      <Modal
        visible={visible}
        onCancel={closeNewSampleModal}
        title={"ADD NEW SAMPLE"}
      >
        <AddSampleForm onSubmitForm={closeNewSampleModal} />
      </Modal>
    </>
  );
}

render(<AddSample />, document.querySelector(".js-add-sample"));

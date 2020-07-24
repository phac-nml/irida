import React, { useEffect, useRef, useState } from "react";
import { render } from "react-dom";
import { Button, Form, Input, Modal } from "antd";
import { IconPlusCircle } from "../../../../components/icons/Icons";
import { grey6, grey9 } from "../../../../styles/colors";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { OntologySelect } from "../../../../components/ontology";
import { TAXONOMY } from "../../../../apis/ontology/taxonomy";
import { useModalBackButton } from "../../../../hooks";
import {
  createNewSample,
  validateSampleName,
} from "../../../../apis/projects/samples";

/**
 * React Ant Design form to create a new sample within a project.
 * @param {function} onSubmit - what should the parent do when the form is submitted
 * @param {boolean} visible - whether the parent modal is open.
 * @returns {JSX.Element}
 * @constructor
 */
function AddSampleForm({ onSubmit, visible = false }) {
  const [form] = Form.useForm();
  const [name, setName] = useState("");
  const [organism, setOrganism] = useState("");
  const nameRef = useRef();

  /**
   * Watch for changes in the modal visibilty. When it changes update the form to reflect.
   * If it opens, focus on the name input
   * If it closes, clear fields
   */
  useEffect(() => {
    if (visible) {
      nameRef.current.focus();
    } else {
      form.resetFields(["name", "organism"]);
      // Special handler for organism since it is rendered slightly different
      // because it is an ontology
      setOrganism("");
    }
  }, [form, visible]);

  /**
   * This is used by Ant Design's input  validation system to server side validate the
   * sample name.  This includes name length, special characters, and if the name is already used.
   * @param rule
   * @param {string} value - the current value of the input
   * @returns {Promise<void>}
   */
  const validateName = async (rule, value) => {
    const response = await validateSampleName(value);
    if (response.status === "error") {
      return Promise.reject(response.help);
    } else {
      return Promise.resolve();
    }
  };

  /**
   * Submit the form and update the UI.
   * @returns {Promise<void>}
   */
  const submit = async () => {
    await createNewSample({ name, organism });

    // Call parent (if this is in a modal, this should close it).
    onSubmit();
    // Need to update the table!
    window.$dt.ajax.reload(null, false);
  };

  return (
    <Form layout={"vertical"} form={form} onFinish={submit}>
      <Form.Item
        name="name"
        label={i18n("AddSample.name")}
        hasFeedback
        rules={[
          () => ({
            validator: validateName,
          }),
        ]}
      >
        <Input
          ref={nameRef}
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      </Form.Item>
      <Form.Item label={i18n("AddSample.organism")} name="organism">
        <OntologySelect
          term={organism}
          ontology={TAXONOMY}
          onTermSelected={(value) => setOrganism(value)}
        />
      </Form.Item>
      <Form.Item>
        <Button type={"primary"} htmlType={"submit"}>
          {i18n("AddSample.submit")}
        </Button>
      </Form.Item>
    </Form>
  );
}

/**
 * React component to add a button to open a modal to create a new sample
 * in a project.
 * @returns {null|JSX.Element}
 * @constructor
 */
function AddSampleButton() {
  const [visible, setVisible] = useState(false);
  const location = setBaseUrl(`/projects/${window.project.id}`);

  /**
   * Open the modal to create a new sample.
   * Add to the browser history in able to allow the back button to
   * close the modal.
   */
  const openNewSampleModal = () => {
    // Allow the user to use the back button.
    window.history.pushState({}, null, `${location}/add-sample`);
    setVisible(true);
  };

  /**
   * Close modal
   * Update the url.
   */
  const closeNewSampleModal = () => {
    // Need to update the url to the original one.
    window.history.pushState({}, null, location);
    setVisible(false);
  };

  useModalBackButton(openNewSampleModal, closeNewSampleModal, "add-sample");

  return window.PAGE.isRemoteProject ? null : (
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
        title={i18n("AddSample.title")}
        footer={null}
      >
        <AddSampleForm onSubmit={closeNewSampleModal} visible={visible} />
      </Modal>
    </>
  );
}

render(<AddSampleButton />, document.querySelector(".js-add-sample"));

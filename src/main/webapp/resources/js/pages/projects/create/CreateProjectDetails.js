import React from "react";
import { Form, Input } from "antd";
import { TAXONOMY } from "../../../apis/ontology/taxonomy";
import { OntologySelect } from "../../../components/ontology";

/**
 * React component to display the form components for the general details
 * of a new project:
 * <ul>
 *   <li>Name</li>
 *   <li>Description</li>
 *   <li>Organism</li>
 *   <li>Remote URL</li>
 * </ul>
 *
 * @param {object} form - Ant Design form instance api.
 * @returns {JSX.Element}
 * @constructor
 */
export function CreateProjectDetails({ form }) {
  /**
   * Creates a handle to the name input, used to set autofocus
   * for when the form is displayed.
   * @type {React.RefObject<unknown>}
   */
  const nameRef = React.createRef();
  const [organism, setOrganism] = React.useState();

  React.useEffect(() => {
    // Autofocus on the name input after loading
    nameRef.current.focus();
  }, [nameRef]);

  const setFormOrganism = (value) => {
    form.setFieldsValue({ organism: value });
  };

  return (
    <>
      <Form.Item
        name="name"
        label={i18n("projects.create.form.name")}
        rules={[{ type: "string", min: 5, required: true }]}
      >
        <Input type={"text"} ref={nameRef} />
      </Form.Item>
      <Form.Item
        name={"organism"}
        label={i18n("projects.create.form.organism")}
      >
        <OntologySelect
          term={organism}
          onTermSelected={setFormOrganism}
          ontology={TAXONOMY}
          autofocus={false}
        />
      </Form.Item>
      <Form.Item
        label={i18n("projects.create.form.description")}
        name="description"
      >
        <Input.TextArea />
      </Form.Item>
      <Form.Item
        name={"remoteURL"}
        label={i18n("projects.create.form.wiki")}
        rules={[{ type: "url", required: false }]}
      >
        <Input type="url" />
      </Form.Item>
    </>
  );
}

import { Form, Input } from "antd";
import React from "react";
import { TAXONOMY } from "../../../apis/ontology/taxonomy";
import { OntologyInput } from "../../../components/ontology";

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
        label={i18n("CreateProjectDetails.name")}
        required
        rules={[
          {
            required: true,
            message: i18n("CreateProjectDetails.name-required"),
          },
          {
            pattern: /^[a-zA-Z0-9\s_-]+$/,
            message: i18n("CreateProjectDetails.name-characters"),
          },
          {
            type: "string",
            min: 5,
            message: i18n("CreateProjectDetails.length"),
          },
        ]}
      >
        <Input className="t-name-input" type={"text"} ref={nameRef} />
      </Form.Item>
      <Form.Item
        name={"organism"}
        label={i18n("projects.create.form.organism")}
      >
        <OntologyInput
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
        <Input.TextArea className="t-desc-input" />
      </Form.Item>
      <Form.Item
        name={"remoteURL"}
        label={i18n("projects.create.form.wiki")}
        rules={[{ type: "url", required: false }]}
      >
        <Input className="t-wiki-input" type="url" />
      </Form.Item>
    </>
  );
}

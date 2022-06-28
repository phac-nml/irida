import React from "react";
import { AutoComplete, Checkbox, Form, Modal } from "antd";

/**
 * Ant Design component to allow the user to save a metadata template.  Allows the
 * user to either give a new name or overwrite the existing name.
 *
 * @param {function} onClose - function to close the modal
 * @param {function} saveTemplate - function to save the template
 * @param {array} templates - list of metadata template on the current project
 * @param {boolean} visible - whether the modal is currently visible
 * @param {object} template - the current state of the template to save
 * @returns {JSX.Element}
 * @constructor
 */
export function SaveTemplateModal({
  onClose,
  saveTemplate,
  templates,
  visible,
  template,
}) {
  const [form] = Form.useForm();

  const [options, setOptions] = React.useState(() =>
    templates.slice(1).map(({ name }) => ({ label: name, value: name }))
  );
  const [existingName, setExistingName] = React.useState(false);

  /**
   * Handle user input.  This will filter the names available based on the current
   * projects templates and provide a filtered list of those names.  Ant Design
   * will allow the user to use the name they enter into the input field if not from
   * one of the templates.
   *
   * @param {string} term - value user from input
   */
  const onSearch = (term) => {
    const lowerTerm = term.toLowerCase();
    const newOptions = templates
      .slice(1)
      .filter((template) => template.name.toLowerCase().includes(lowerTerm))
      .map(({ name }) => ({ label: name, value: name }));
    setOptions(newOptions);
  };

  /**
   * When the user click ok, validate the name again and make sure all is good.
   * When it is good submit to the server and then close the window.  Rest is
   * handles by the parent components.
   */
  const onOk = () => {
    form.validateFields().then((values) => {
      const fields = template.modified.filter((t) => !t.hide);
      let id = undefined;
      if (values.overwrite) {
        id = templates.find(({ name }) => name === values.name).id;
      }
      saveTemplate(values.name, fields, id);
      form.resetFields();
      onClose();
    });
  };

  /**
   * Validate the input template name against existing template names in this project
   *
   * @param {object} _ - Ant Design Form.Item object
   * @param {String} value - Value from the input
   * @returns {Promise<boolean>}
   */
  const existingValidator = async (_, value) => {
    const overwrite = form.getFieldValue("overwrite");
    const found = templates.findIndex(({ name }) => name === value) > -1;
    setExistingName(found);
    if (!overwrite && found) {
      return Promise.reject();
    }
    return Promise.resolve(true);
  };

  return (
    <Modal
      title={i18n("linelist.templates.saveModal.title")}
      visible={visible}
      onCancel={onClose}
      onOk={onOk}
      okButtonProps={{
        className: "t-modal-save-template-btn",
      }}
      className="t-save-modal"
    >
      <Form
        layout="vertical"
        form={form}
        initialValues={{ name: "", overwrite: false }}
      >
        <Form.Item
          label={i18n("linelist.templates.saveModal.name")}
          name="name"
          rules={[
            {
              required: true,
              message: i18n("linelist.templates.saveModal.required"),
            },
            {
              min: 5,
              message: i18n("linelist.templates.saveModal.length"),
            },
            {
              validator: existingValidator,
              message: i18n("linelist.templates.saveModal.nameExists"),
            },
          ]}
        >
          <AutoComplete
            options={options}
            allowClear={true}
            backfill={true}
            showSearch
            notFoundContent={null}
            onSearch={onSearch}
          />
        </Form.Item>
        <Form.Item name="overwrite" valuePropName="checked">
          <Checkbox
            disabled={!existingName}
            onChange={() => form.validateFields(["name"])}
          >
            {i18n("linelist.templates.saveModal.overwrite")}
          </Checkbox>
        </Form.Item>
      </Form>
    </Modal>
  );
}

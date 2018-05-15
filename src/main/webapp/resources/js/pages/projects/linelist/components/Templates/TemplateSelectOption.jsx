import React from "react";
import PropTypes from "prop-types";
import { SaveTemplateButton } from "./SaveTemplateButton";
import { Button, Popconfirm, Tag } from "antd";

const { i18n } = window.PAGE;

export function TemplateSelectOption(props) {
  const { template, modified, index, current, saved } = props;
  const { name, fields, id } = template;

  function renderUpdateSave() {
    if (id === -1) {
      return (
        <SaveTemplateButton
          showSaveModal={props.showSaveModal}
          saveTemplate={props.saveTemplate}
        />
      );
    }
    return <UpdateTemplateButton {...props} />;
  }

  return (
    <React.Fragment>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <span
          style={{
            maxWidth: 170,
            overflow: "hidden",
            textOverflow: "ellipsis"
          }}
        >
          {name}
        </span>
        <span>
          {saved && index === current ? <Tag color="green">SAVED</Tag> : null}
          {modified !== null && modified.name === name
            ? renderUpdateSave()
            : null}
          {index > 0 ? (
            <Tag className="field-count">{fields.length}</Tag>
          ) : null}
        </span>
      </div>
    </React.Fragment>
  );
}

TemplateSelectOption.propTypes = {
  template: PropTypes.object.isRequired,
  saved: PropTypes.bool.isRequired,
  current: PropTypes.number.isRequired,
  index: PropTypes.number.isRequired,
  modified: PropTypes.object,
  saveTemplate: PropTypes.func.isRequired,
  showSaveModal: PropTypes.func.isRequired
};

class UpdateTemplateButton extends React.Component {
  constructor(props) {
    super(props);
  }

  saveTemplate = () => {
    const { name, fields, id } = this.props.modified;
    this.props.saveTemplate(name, fields, id);
  };

  render() {
    return (
      <Popconfirm
        title="__Update this template?__"
        onConfirm={this.saveTemplate}
        okText="__UPDATE__"
        cancelText="__CANCEL__"
      >
        <Button type="dashed" size="small" onClick={e => e.stopPropagation()}>
          UPDATE
        </Button>
      </Popconfirm>
    );
  }
}

UpdateTemplateButton.propTypes = {
  modified: PropTypes.object.isRequired,
  saveTemplate: PropTypes.func.isRequired
};

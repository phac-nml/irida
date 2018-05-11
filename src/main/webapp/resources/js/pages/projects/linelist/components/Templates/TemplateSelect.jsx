import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { Tag, Select } from "antd";
import { MODIFIED_SELECT_INDEX } from "../../reducers/templates";

const { Option } = Select;
const { i18n } = window.PAGE;

export class TemplateSelect extends React.Component {
  constructor(props) {
    super(props);
  }

  templateSelected = index => {
    if (this.props.current !== index || (this.props.current === index && this.props.modified)) {
      this.props.useTemplate(index);
    }
  };

  render() {
    const templates = this.props.templates.toJS();

    return templates.length > 0 ? (
      <React.Fragment>
        <Select
          disabled={templates.length === 0}
          value={
            this.props.modified !== null
              ? MODIFIED_SELECT_INDEX
              : this.props.current
          }
          style={{ width: 250 }}
          onSelect={this.templateSelected}
        >
          {this.props.modified ? (
            <Option value={MODIFIED_SELECT_INDEX}>
              {i18n.linelist.templates.Select.modified}
            </Option>
          ) : null}
          {templates.map((t, index) => (
            <Option key={t.id} value={index}>
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <span
                  style={{
                    maxWidth: 170,
                    overflow: "hidden",
                    textOverflow: "ellipsis"
                  }}
                >
                  {t.name}
                </span>
                {t.fields.length > 0 ? <Tag>{t.fields.length}</Tag> : null}
              </div>
            </Option>
          ))}
        </Select>
      </React.Fragment>
    ) : null;
  }
}

TemplateSelect.propTypes = {
  current: PropTypes.number.isRequired,
  modified: PropTypes.object,
  templates: ImmutablePropTypes.list.isRequired,
  useTemplate: PropTypes.func.isRequired
};

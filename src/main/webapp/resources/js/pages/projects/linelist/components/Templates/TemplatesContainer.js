import { connect } from "react-redux";
import { TemplateSelect } from "./TemplateSelect";

const mapStateToProps = state => ({
  templates: state.templates.templates,
  current: state.templates.current
});

const mapDispatchToProps = dispatch => ({});

export const TemplatesContainer = connect(mapStateToProps, mapDispatchToProps)(
  TemplateSelect
);

import { connect } from "react-redux";
import { Templates } from "./Templates";
import { actions } from "../../redux/modules/template";

const mapStateToProps = state => ({
  templates: state.templates.templates,
  current: state.template.id,
  modified: state.template.modified,
  validating: state.template.validating
});

const mapDispatchToProps = dispatch => ({
  fetchTemplate: id => dispatch(actions.fetchTemplate(id)),
  validateTemplateName: name => dispatch(actions.validateTemplateName(name))
});

export const TemplatesContainer = connect(mapStateToProps, mapDispatchToProps)(
  Templates
);

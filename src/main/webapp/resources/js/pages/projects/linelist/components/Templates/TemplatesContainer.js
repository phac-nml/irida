import { connect } from "react-redux";
import { actions } from "../../reducers/template";
import { Templates } from "./Templates";

const mapStateToProps = state => ({
  templates: state.templates.get("templates"),
  current: state.template.get("current"),
  modified: state.template.get("modified"),
  validating: state.template.get("validating")
});

const mapDispatchToProps = dispatch => ({
  fetchTemplate: id => dispatch(actions.load(id)),
  validateTemplateName: name => dispatch(action.validateTemplateName(name))
});

export const TemplatesContainer = connect(mapStateToProps, mapDispatchToProps)(
  Templates
);

import { connect } from "react-redux";
import { TemplateSelect } from "./TemplateSelect";
import { actions } from "../../redux/modules/templates";

const mapStateToProps = state => ({
  templates: state.templates.templates,
  current: state.templates.current
});

const mapDispatchToProps = dispatch => ({
  fetchTemplate: id => dispatch(actions.fetchTemplate(id))
});

export const TemplatesContainer = connect(mapStateToProps, mapDispatchToProps)(
  TemplateSelect
);

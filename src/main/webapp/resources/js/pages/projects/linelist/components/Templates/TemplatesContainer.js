import { connect } from "react-redux";
import { TemplateSelect } from "./TemplateSelect";
import { actions } from "../../reducers/template";

const mapStateToProps = state => ({
  templates: state.templates.get("templates"),
  current: state.template.get("current")
});

const mapDispatchToProps = dispatch => ({
  fetchTemplate: id => dispatch(actions.load(id))
});

export const TemplatesContainer = connect(mapStateToProps, mapDispatchToProps)(
  TemplateSelect
);

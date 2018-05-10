import { connect } from "react-redux";
import { TemplateSelect } from "./TemplateSelect";
import { actions } from "../../reducers/templates";

const mapStateToProps = state => ({
  templates: state.templates.get("templates"),
  current: state.templates.get("current")
});

const mapDispatchToProps = dispatch => ({
  useTemplate: index => dispatch(actions.use(index))
});

export const TemplatesContainer = connect(mapStateToProps, mapDispatchToProps)(
  TemplateSelect
);

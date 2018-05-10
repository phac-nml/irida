import { connect } from "react-redux";
import { actions } from "../../reducers/templates";
import { Templates } from "./Templates";

const mapStateToProps = state => ({
  templates: state.templates.get("templates"),
  current: state.templates.get("current"),
  modified: state.templates.get("modified")
});

const mapDispatchToProps = dispatch => ({
  useTemplate: index => dispatch(actions.use(index))
});

export const TemplatesContainer = connect(mapStateToProps, mapDispatchToProps)(
  Templates
);

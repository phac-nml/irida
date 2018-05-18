import { connect } from "react-redux";
import { ToolPanel } from "./ToolPanel";
import { actions } from "../../../reducers/templates";

const mapStateToProps = state => ({
  templates: state.templates.get("templates"),
  current: state.templates.get("current")
});
const mapDispatchToProps = dispatch => ({
  templateModified: fields => dispatch(actions.templateModified(fields))
});

export const ToolPanelContainer = connect(mapStateToProps, mapDispatchToProps)(
  ToolPanel
);

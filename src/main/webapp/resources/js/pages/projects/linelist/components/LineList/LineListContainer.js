import { LineList } from "./LineList";
import { connect } from "react-redux";

/*
Default react-redux boiler plate to connect the current state of the
application to the component. When the state of the application gets
updated (in this case so far it is the loading state), the this connect
method is what triggers the updates.
 */
const mapStateToProps = state => ({
  initializing: state.fields.get("initializing"),
  error: state.fields.get("error")
});

const mapDispatchToProps = dispatch => ({});

export const LineListContainer = connect(mapStateToProps, mapDispatchToProps)(
  LineList
);

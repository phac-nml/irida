import { AutoComplete } from "antd";
import React, { useEffect, useRef, useState } from "react";
import { searchOntology } from "../../apis/ontology/taxonomy";
import { useDebounce } from "../../hooks";

const { Option } = AutoComplete;

/**
 * Component to render a AutoComplete input to search for a term in an ontology.
 *
 * @param {string} term - initial value
 * @param {function} onTermSelected - callback for when a term is selected
 * @param {string} ontology - which ontology to query
 * @param {boolean} autofocus - automatically focus on the input when loaded
 * @returns {*}
 * @constructor
 */
export function OntologyInput({
  term,
  onTermSelected,
  ontology,
  autofocus = true,
}) {
  const [options, setOptions] = useState([]);
  const [query, setQuery] = useState("");
  const selectRef = useRef();

  /**
   * Reducer: Create the dropdown contents from the taxonomy.
   *
   * @param {array} accumulator
   * @param {object} current
   * @returns {*}
   */
  function optionsReducer(accumulator, current) {
    accumulator.push(
      <Option key={current.value} value={current.value}>
        {current.text}
      </Option>
    );
    /*
    Recursively check to see if there are any children to add
     */
    if (current.children) {
      accumulator.push(...current.children.reduce(optionsReducer, []));
    }
    return accumulator;
  }

  /*
  Since we don't want a post being send until the user is done typing, set a
  delay when to send the request based on the last typed letter.
   */
  const debouncedQuery = useDebounce(query, 350);

  useEffect(() => {
    /*
    Accessed only when the value of debouncedQuery changes.  This will
    trigger a server call & updated the list.
     */
    if (query.length >= 2) {
      searchOntology({ query: debouncedQuery, ontology }).then((data) => {
        setOptions(data.reduce(optionsReducer, []));
      });
    }
  }, [debouncedQuery]);

  useEffect(() => {
    if (autofocus) {
      /*
    Focus on the input when the component is mounted.
     */
      selectRef.current.focus();
    }
  }, [autofocus]);

  /**
   * Allow the user to arbitrarily set the value if not an option.
   *
   * @param {SyntheticEvent} e React input synthetic event for input
   */
  const onBlur = (e) => onTermSelected(e.target.value);

  return (
    <AutoComplete
      className="t-organism-input"
      allowClear={true}
      backfill={true}
      ref={selectRef}
      showSearch
      defaultValue={term || ""}
      notFoundContent={null}
      onSearch={setQuery}
      onSelect={onTermSelected}
      onBlur={onBlur}
      onClear={onTermSelected}
      style={{ width: "100%" }}
    >
      {options}
    </AutoComplete>
  );
}

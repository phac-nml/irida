import React, { useEffect, useRef, useState } from "react";
import { Select } from "antd";
import { searchOntology } from "../../apis/ontology/taxonomy";
import { useDebounce } from "../../hooks";

const { Option } = Select;

/**
 * Component to render a select input to search for a term in an ontology.
 *
 * @param {string} term - initial value
 * @param {function} onTermSelected - callback for when a term is selected
 * @param {string} ontology - which ontology to query
 * @returns {*}
 * @constructor
 */
export function OntologySelect({ term, onTermSelected, ontology }) {
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
    /*
    Focus on the select input when the component is mounted.
     */
    selectRef.current.focus();
  }, []);

  return (
    <Select
      ref={selectRef}
      showSearch
      defaultValue={term || ""}
      notFoundContent={null}
      onSearch={setQuery}
      onSelect={onTermSelected}
      style={{ width: "100%" }}
    >
      {options}
    </Select>
  );
}

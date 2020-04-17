import React, { useEffect, useRef, useState } from "react";
import { Select } from "antd";
import { searchTaxonomy } from "../../apis/taxonomy/taxonomy";
import { useDebounce } from "../../hooks";

const { Option } = Select;

export function OntoSelect({ organism, setOrganism }) {
  const [options, setOptions] = useState([]);
  const [query, setQuery] = useState("");
  const selectRef = useRef();

  useEffect(() => {
    selectRef.current.focus();
  }, []);

  function optionsReducer(accumulator, current) {
    accumulator.push(
      <Option key={current.value} value={current.value}>
        {current.text}
      </Option>
    );
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
    searchTaxonomy(debouncedQuery).then((data) => {
      setOptions(data.reduce(optionsReducer, []));
    });
  }, [debouncedQuery]);

  return (
    <Select
      ref={selectRef}
      showSearch
      defaultValue={organism || ""}
      notFoundContent={null}
      onSearch={setQuery}
      onSelect={setOrganism}
      style={{ width: "100%" }}
    >
      {options}
    </Select>
  );
}

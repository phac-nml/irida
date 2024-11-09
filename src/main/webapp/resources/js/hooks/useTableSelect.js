import React from "react";

/**
 * React hook to get the select items from an Ant Design table.
 * Ant Design on returns the selected items key.  This translates that key
 * into the actual list item.
 * @param {Array} list - items rendered as the dataSource to the table.
 * @returns {[{selected: *[], selectedItems: *[]}, {setSelected: (value: (((prevState: *[]) => *[]) | *[])) => void}]}
 */
export function useTableSelect(list = []) {
  const [selected, setSelected] = React.useState([]);
  const [selectedItems, setSelectedItems] = React.useState([]);

  React.useEffect(() => {
    if (list && selected.length) {
      const set = new Set(selected);
      setSelectedItems(list.filter((item) => set.has(item.key)));
    }
  }, [list, selected]);

  return [{ selected, selectedItems }, { setSelected }];
}
